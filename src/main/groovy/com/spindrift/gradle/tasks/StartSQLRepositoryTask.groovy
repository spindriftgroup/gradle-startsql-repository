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
import com.spindrift.gradle.process.ExecutionResult
import com.spindrift.gradle.process.Executor
import com.spindrift.gradle.process.ScriptExecutor
import com.spindrift.gradle.config.InvalidNamedConfigurationException

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.GradleException

class StartSQLRepositoryTask extends DefaultTask {

  static final String TASK_DESCRIPTION = "Executes the startSQLRepository utility."
  static final String TASK_GROUP = "ATG"

  Executor executor

  StartSQLRepositoryTask() {
    description = TASK_DESCRIPTION
    group = TASK_GROUP
    executor = new ScriptExecutor()
  }

  @TaskAction
  void executeCommandLine() {
    if (project.hasProperty("configName")) {
      executeSingleCommand(project.configName)
    }
    else {
      executeMultipleCommands()
    }

  }

  private void executeSingleCommand(String name) {
    validateNamedScript(name)
    project.startSQLRepository.configurations.configurations.each.filter { it.name == name} { script ->
      outputExecutionMessage()
      executeCommand(script)
    }
  }

  def validateNamedScript(String name) {
    boolean valid = false
    project.startSQLRepository.configurations.configurations.each { script ->
      valid = (script.name == name)
    }
    if (!valid) throw new InvalidNamedConfigurationException("startSQLRepository configuration with [name=$name] not found.")
  }

  private void executeMultipleCommands() {
    project.startSQLRepository.configurations.configurations.each { script ->
      outputExecutionMessage()
      executeCommand(script)
    }
  }

  def executeCommand(ScriptConfiguration script) {
    ExecutionResult result
    def before = System.currentTimeMillis()
    result = executor.execute(script)
    def after = System.currentTimeMillis()
    double timeInSeconds = (after - before) / 1000D

    if (result.isSuccessful()) {
      project.logger.lifecycle "startSQLRepository executed ${script.repository} in ${timeInSeconds} secs"
      if (result.hasOutput()) {
        project.logger.info result.output
      }
    } else {
      String errorOutput = "${result.output}\n${result.errorText}"
      throw new GradleException("Script execution failed with code:${result.exitValue}\n${errorOutput}")
    }
  }

  def outputExecutionMessage() {
    project.logger.lifecycle "Executing: startSQLRepository"
  }

}
