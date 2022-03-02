package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.DatabaseOptions;
import com.tigrisdata.db.client.model.TigrisDBResponse;

import java.util.List;

public interface TigrisDBClient {

  /**
   * Retrieves the database instance
   *
   * @param databaseName databaseName
   * @return an instance of {@link TigrisDatabase}
   * @throws TigrisDBException if database doesn't exist, authentication failure or any other error
   */
  TigrisDatabase getDatabase(String databaseName) throws TigrisDBException;

  /**
   * Lists the available databases for the current user.
   *
   * @param listDatabaseOptions
   * @return a list of @{@link TigrisDatabase}
   * @throws TigrisDBException authentication failure or any other error
   */
  List<TigrisDatabase> listDatabases(DatabaseOptions listDatabaseOptions) throws TigrisDBException;

  /**
   * Creates the database
   *
   * @param databaseName name of the database
   * @param databaseOptions options
   * @return an instance of {@link TigrisDBResponse} from server
   * @throws TigrisDBException in case of auth error or any other failure.
   */
  TigrisDBResponse createDatabase(String databaseName, DatabaseOptions databaseOptions)
      throws TigrisDBException;

  /**
   * Drops the database
   *
   * @param databaseName name of the database
   * @param databaseOptions options
   * @return an instance of {@link TigrisDBResponse} from server
   * @throws TigrisDBException in case of auth error or any other failure.
   */
  TigrisDBResponse dropDatabase(String databaseName, DatabaseOptions databaseOptions)
      throws TigrisDBException;
}
