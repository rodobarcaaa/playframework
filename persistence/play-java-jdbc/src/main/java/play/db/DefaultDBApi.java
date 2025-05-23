/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.db;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Map;
import play.libs.Scala;

/** Default delegating implementation of the DB API. */
@Singleton
public class DefaultDBApi implements DBApi {

  private final play.api.db.DBApi dbApi;
  private final List<Database> databases;
  private final Map<String, Database> databaseByName;

  @Inject
  public DefaultDBApi(play.api.db.DBApi dbApi) {
    this.dbApi = dbApi;

    ImmutableList.Builder<Database> databases = new ImmutableList.Builder<>();
    ImmutableMap.Builder<String, Database> databaseByName = new ImmutableMap.Builder<>();
    for (play.api.db.Database db : Scala.asJava(dbApi.databases())) {
      Database database = new DefaultDatabase(db);
      databases.add(database);
      databaseByName.put(database.getName(), database);
    }
    this.databases = databases.build();
    this.databaseByName = databaseByName.build();
  }

  public List<Database> getDatabases() {
    return databases;
  }

  public Database getDatabase(String name) {
    return databaseByName.get(name);
  }

  public void shutdown() {
    dbApi.shutdown();
  }
}
