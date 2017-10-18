/**
 * Copyright (C) 2012-2017 Spindrift B.V. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spindrift.gradle.config

/**
 * A container for optional parameters
 * @author hallatech
 */
import groovy.transform.AutoClone

@AutoClone
class Options {

  public static final List<String> DATABASE_OPTIONS = ['oracle','sybase','solid','informix','db2','mysql']
  public static final List<String> IMPORT_OPTIONS = ['workflow', 'activity', 'project', 'workspace', 'user', 'comment']

  String database
  boolean debug = false
  boolean verboseSQL = false
  boolean noTransaction = false
  boolean skipReferences = false
  String encoding
  String project
  String workflow
  String activity
  String workspace
  String comment
  String user


  void debug(boolean debug) {
    this.debug = debug
  }
  void verboseSQL(boolean verboseSQL) {
    this.verboseSQL = verboseSQL
  }
  void noTransaction(boolean noTransaction) {
    this.noTransaction = noTransaction
  }
  void skipReferences(boolean skipReferences) {
    this.skipReferences = skipReferences
  }
  void encoding(String encoding) {
    this.encoding = encoding
  }
  void project(String project) {
    this.project = project
  }
  void workflow(String workflow) {
    this.workflow = workflow
  }
  void activity(String activity) {
    this.activity = activity
  }
  void workspace(String workspace) {
    this.workspace = workspace
  }
  void comment(String comment) {
    this.comment = comment
  }
  void user(String user) {
    this.user = user
  }

  def validate() {
    if (database && !(database in DATABASE_OPTIONS)) {
      throw new InvalidConfigurationException(
        "The option value for [database=${database}] must be one of ${DATABASE_OPTIONS}.")
    }
    if (workflow && !this.project) {
      throw new InvalidConfigurationException(
        "The option value [workflow=${workflow}] is invalid without the project option.")
    }
    if (activity && !this.project) {
      throw new InvalidConfigurationException(
        "The option value [activity=${activity}] is invalid without the project option.")
    }
    if (project && !user) {
      throw new InvalidConfigurationException(
        "The option value [user=<username>] is required if the project option is specified.")
    }
  }

  def validateForCommand(String command) {
    if (command != 'import' && (workflow || activity || this.project || workspace || user || comment)) {
      throw new InvalidConfigurationException("The option values ${IMPORT_OPTIONS} "
          .concat("can only be used with the import command. ")
          .concat("The [command=$command] is invalid in this configuration."))
    }
    if (!(command in ScriptConfiguration.VALID_EXPORT_COMMANDS) && (skipReferences)) {
      throw new InvalidConfigurationException( "The option value [skipReferences] is only valid for "
        .concat("${ScriptConfiguration.VALID_EXPORT_COMMANDS} commands."))
    }

  }
  
  /**
   * @return a list of options broken down into option key followed by option value
   * and where required in the order expected
   */
  public List<String> list() {
    List opts=[]
    if (database) {
      opts << '-database'
      opts << database
    }
    if (debug) opts << '-debug'
    if (verboseSQL) opts << '-verboseSQL'
    if (noTransaction) opts << '-noTransaction'
    if (skipReferences) opts << '-skipReferences'
    if (encoding) {
      opts << '-encoding'
      opts << encoding
    }
    if (project) {
      opts << '-project'
      opts << project
    }
    if (workflow) {
      opts << '-workflow'
      opts << workflow
    }
    if (activity) {
      opts << '-activity'
      opts << activity
    }
    if (workspace) {
      opts << '-workspace'
      opts << workspace
    }
    if (comment) {
      opts << '-comment'
      opts << comment
    }
    if (user) {
      opts << '-user'
      opts << user
    }
    opts
  }
}
