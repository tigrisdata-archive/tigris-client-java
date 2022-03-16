package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.DatabaseOptions;
import com.tigrisdata.db.client.model.TigrisDBResponse;

import java.io.Closeable;
import java.util.List;

public interface TigrisDBClient extends Closeable {

  /**
   * Retrieves the database instance
   *
   * @param databaseName databaseName
   * @return an instance of {@link TigrisDatabase}
   */
  TigrisDatabase getDatabase(String databaseName);

  /**
   * Lists the available databases for the current user.
   *
   * @param listDatabaseOptions options
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
