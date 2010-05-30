/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.kernel.modules.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public final class SearchHandler extends AbstractHandler {

    /** Default lucene field. */
    public static final String DEFAULT_INDEX = "any";

    /** Dummy lucene field to retrieve all elements. */
    private static final String INDEX_FIELD_DUMMY = "dummy";

    /** Singleton instance. */
    private static SearchHandler instance = new SearchHandler();

    private final class RefreshSearchIndexSwingWorker extends
			SwingWorker<Void, Void> {
		private final SearchableObject searchableObject;
		private IndexWriter indexWriter;

		private RefreshSearchIndexSwingWorker(SearchableObject searchableObject) {
			this.searchableObject = searchableObject;
		}

		@Override
		protected Void doInBackground() {
		    ReadWriteLock searchIndexLock = indexLocks.get(searchableObject);
		    try {
		        searchIndexLock.writeLock().lock();
		        initSearchIndex();
		        updateSearchIndex(searchableObject.getElementsToIndex());
		        finishSearchIndex();
		        return null;
		    } finally {
		        searchIndexLock.writeLock().unlock();
		        ClosingUtils.close(indexWriter);
		        currentIndexingWorks.put(searchableObject, Boolean.FALSE);
		    }
		}

		@Override
		protected void done() {
		    // Nothing to do
		}

		private void initSearchIndex() {
		    getLogger().info(LogCategories.HANDLER, "Updating index for " + searchableObject.getClass());
		    try {
		        FileUtils.deleteDirectory(searchableObject.getIndexDirectory().getFile());
		        indexWriter = new IndexWriter(searchableObject.getIndexDirectory(), new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
		    } catch (CorruptIndexException e) {
		        getLogger().error(LogCategories.HANDLER, e);
		    } catch (LockObtainFailedException e) {
		        getLogger().error(LogCategories.HANDLER, e);
		    } catch (IOException e) {
		        getLogger().error(LogCategories.HANDLER, e);
		    }
		}

		private void updateSearchIndex(List<AudioObject> audioObjects) {
		    getLogger().info(LogCategories.HANDLER, "update search index");
		    if (indexWriter != null) {
		        for (AudioObject audioObject : audioObjects) {
		            Document d = searchableObject.getDocumentForElement(audioObject);
		            // Add dummy field
		            d.add(new Field(INDEX_FIELD_DUMMY, INDEX_FIELD_DUMMY, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));

		            try {
		                indexWriter.addDocument(d);
		            } catch (CorruptIndexException e) {
		                getLogger().error(LogCategories.HANDLER, e);
		            } catch (IOException e) {
		                getLogger().error(LogCategories.HANDLER, e);
		            }
		        }
		    }
		}

		private void finishSearchIndex() {
		    getLogger().info(LogCategories.HANDLER, StringUtils.getString("Update index for ", searchableObject.getClass(), " finished"));
		    if (indexWriter != null) {
		        try {
		            indexWriter.optimize();
		            indexWriter.close();

		            indexWriter = null;
		        } catch (CorruptIndexException e) {
		            getLogger().error(LogCategories.HANDLER, e);
		        } catch (IOException e) {
		            getLogger().error(LogCategories.HANDLER, e);
		        }
		    }
		}
	}

	/**
     * Logical operators used to create complex rules.
     */
    public enum LogicalOperator {
        AND, OR, NOT
    }

    /** List of searchable objects available. */
    private List<SearchableObject> searchableObjects;

    /** List of operators for simple rules. */
    private List<String> searchOperators;

    /**
     * List which indicates if an indexing process is currently running for a
     * given searchable object.
     */
    private volatile Map<SearchableObject, Boolean> currentIndexingWorks;

    /** Map with locks for each index. */
    private Map<SearchableObject, ReadWriteLock> indexLocks;

    /**
     * Constructor.
     */
    private SearchHandler() {
    }

    @Override
    public void applicationFinish() {
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
    }

    @Override
    public void applicationStarted() {
    }

    @Override
    protected void initHandler() {
        currentIndexingWorks = new HashMap<SearchableObject, Boolean>();
        searchableObjects = new ArrayList<SearchableObject>();
        indexLocks = new HashMap<SearchableObject, ReadWriteLock>();

        searchOperators = new ArrayList<String>();
        searchOperators.add(":");
    }

    /**
     * Method to register a searchable object. All searchable objects must call
     * this method to initialize searches
     * 
     * @param so
     *            the so
     */
    public void registerSearchableObject(SearchableObject so) {
        currentIndexingWorks.put(so, Boolean.FALSE);
        indexLocks.put(so, new ReentrantReadWriteLock(true));
        searchableObjects.add(so);
    }

    /**
     * Method to unregister a searchable object. After this method is called,
     * searchable object is not searchable, and must be registered again
     * 
     * @param so
     */
    public void unregisterSearchableObject(SearchableObject so) {
        currentIndexingWorks.remove(so);
        indexLocks.remove(so);
        searchableObjects.remove(so);
    }

    /**
     * Returns SearchHandler unique instance.
     * 
     * @return the instance
     */
    public static SearchHandler getInstance() {
        return instance;
    }

    /**
     * Starts a search by updating indexes and showing search dialog.
     */
    public void startSearch() {
        // Updates indexes
        for (SearchableObject searchableObject : searchableObjects) {
            updateSearchIndex(searchableObject);
        }

        // Set list of searchable objects
        ControllerProxy.getInstance().getCustomSearchController().setListOfSearchableObjects(searchableObjects);

        // Set list of operators
        ControllerProxy.getInstance().getCustomSearchController().setListOfOperators(searchOperators);

        // Show dialog to start search
        ControllerProxy.getInstance().getCustomSearchController().showSearchDialog();
    }

    /**
     * Evaluates a search query and returns a result list of audio objects and
     * scores (0 <= score <= 1).
     * 
     * @param queryStr
     *            The search query
     * @param searchableObject
     *            the searchable object
     * 
     * @return The search result
     * 
     * @throws SearchIndexNotAvailableException
     *             If no search index was found
     * @throws SearchQuerySyntaxException
     *             If the search query has invalid syntax
     */
    public List<AudioObject> search(SearchableObject searchableObject, String queryStr) throws SearchIndexNotAvailableException, SearchQuerySyntaxException {
        ReadWriteLock searchIndexLock = indexLocks.get(searchableObject);
        Searcher searcher = null;
        try {
            searchIndexLock.readLock().lock();

            String queryString = applyQueryTransformations(queryStr);

            Query query = new QueryParser(Version.LUCENE_CURRENT, DEFAULT_INDEX, new SimpleAnalyzer()).parse(queryString);
            searcher = new IndexSearcher(searchableObject.getIndexDirectory(), true);
            TopDocs topDocs = searcher.search(query, 1000);
            List<RawSearchResult> rawSearchResults = new ArrayList<RawSearchResult>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                rawSearchResults.add(new RawSearchResult(searcher.doc(scoreDoc.doc), scoreDoc.score));
            }
            List<AudioObject> result = searchableObject.getSearchResult(rawSearchResults);
            getLogger().debug(LogCategories.REPOSITORY, "Query: ", queryString, " (", result.size(), " search results)");
            return result;
        } catch (IOException e) {
            throw new SearchIndexNotAvailableException();
        } catch (ParseException e) {
            throw new SearchQuerySyntaxException();
        } finally {
            ClosingUtils.close(searcher);
            searchIndexLock.readLock().unlock();
        }
    }

    /**
     * Applies a set of transformations to match Lucene syntax.
     * 
     * @param query
     *            the query
     * 
     * @return Query transformed in Lucene language
     */
    private String applyQueryTransformations(String query) {
        String tempQuery;

        // Replace "NOT xx" by Lucene syntax: "dummy:dummy NOT xx"
        tempQuery = query.replaceAll(LogicalOperator.NOT.toString(), StringUtils.getString(INDEX_FIELD_DUMMY, ":", INDEX_FIELD_DUMMY, " NOT"));

        // Find numeric values and replace to Lucene numeric ranges:
        // year: 2000 ---> year: [2000 TO 2000]
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(tempQuery, " ");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.matches("\"[0-9]+\"")) {
                sb.append(StringUtils.getString("[", token, " TO ", token, "]"));
            } else {
                sb.append(token);
            }
            sb.append(" ");
        }

        return sb.toString();
    }

    /**
     * Generic method to update any searchable object.
     * 
     * @param searchableObject
     *            the searchable object
     */
    private void updateSearchIndex(final SearchableObject searchableObject) {
        SwingWorker<Void, Void> refreshSearchIndex = new RefreshSearchIndexSwingWorker(searchableObject);
        if (currentIndexingWorks.get(searchableObject) == null || !currentIndexingWorks.get(searchableObject)) {
            currentIndexingWorks.put(searchableObject, Boolean.TRUE);
            refreshSearchIndex.execute();
        }

    }

}
