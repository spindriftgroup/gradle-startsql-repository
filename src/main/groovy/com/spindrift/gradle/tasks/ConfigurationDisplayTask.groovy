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
package com.spindrift.gradle.tasks

import com.spindrift.gradle.config.ScriptConfiguration
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ConfigurationDisplayTask extends DefaultTask {


  static final String TASK_DESCRIPTION = "Displays the startSQLRepository task configurations."
  static final String TASK_GROUP = "ATG"

  ConfigurationDisplayTask() {
    description = TASK_DESCRIPTION
    group = TASK_GROUP
  }

  @TaskAction
  void displayConfiguration() {
    def levelTab = "\\---"
    output "-"*40
    output "startSQLRepository configurations"
    output "-"*40
    project.startSQLRepository.configurations.configurations.eachWithIndex { script, index ->
      output("")
      if (script.name) {
        output("name: ${script.name}")
      }
      else {
        output("un-named: ${index}")
      }
      output "${levelTab} repository: ${script.repository}"
      output "${levelTab} command: ${script.command}"
      if (script.modules) {
        output "${levelTab} modules: ${script.modules}"
      }
      if (script.server) {
        output "${levelTab} server: ${script.server}"
      }
      if (script.file) {
        output "${levelTab} file i-o: ${script.file}"
      }
      if (script.itemTypes) {
        output "${levelTab} item-types: ${script.itemTypes}"
      }
      ScriptConfiguration scriptConfiguration = script
      output("${levelTab} CLI args: ${scriptConfiguration.commandLine().join(' ')}")
    }
  }

  def output(line) {
    project.logger.quiet line
  }
}
