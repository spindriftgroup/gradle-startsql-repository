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
package com.spindrift.gradle.plugins

import com.spindrift.gradle.tasks.ConfigurationDisplayTask
import com.spindrift.gradle.tasks.StartSQLRepositoryTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Adds startSQLRepository wrapper task and configuration to a gradle project.
 * Configuration defaults and overrides are made via the gradle extension mechanism
 *
 * @author hallatech
 *
 */
class StartSQLRepositoryPlugin implements Plugin<Project> {

  static final String PLUGIN_EXTENSION_NAME="startSQLRepository"
  static final String START_SQL_REPOSITORY_TASK="startSQLRepository"
  static final String SHOW_CONFIGURATIONS_TASK="showConfigurations"

  @Override
  void apply(Project project) {
    project.extensions."${PLUGIN_EXTENSION_NAME}" = new StartSQLRepositoryPluginExtension()
    addStartSQLRepositoryTask(project)
    addShowConfigurationsTask(project)
  }

  def addStartSQLRepositoryTask(Project project) {
    project.task(START_SQL_REPOSITORY_TASK, type: StartSQLRepositoryTask )
  }

  def addShowConfigurationsTask(Project project) {
    project.task(SHOW_CONFIGURATIONS_TASK, type: ConfigurationDisplayTask)
  }



}
