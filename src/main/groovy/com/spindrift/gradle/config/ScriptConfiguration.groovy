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
 * A container for a full set of script configuration parameters.
 * It contains a builder to build a configuration because of optional parameters, which includes simple validation of the
 * optional required parameters. The extra options are not validated and left to the invoked script to validate itself.
 * Requesting the commandLine method should return a groovy style executable list of strings relevant to the operating system
 * 
 * @author hallatech
 */
import org.gradle.util.ConfigureUtil

import com.spindrift.gradle.os.OSUtils

import groovy.transform.AutoClone

@AutoClone
class ScriptConfiguration {
  private static String SCRIPT_NAME='startSQLRepository'
  private static String WIN_SCRIPT_NAME="${SCRIPT_NAME}.bat"
  private static String MISSING_DEFAULT_PARAMETERS_ERROR_MSG="Failed to create a new ScriptConfiguration. Default parameter repository or command is missing from configuration."
  private static final List<String> VALID_COMMANDS=['outputSQL','outputSQLFile','import','export','exportRepositories']
  private static String INVALID_COMMAND_ERROR_MSG="Invalid command specified. Use one of ${VALID_COMMANDS}"
  private static String MISSING_PARAMETERS_ERROR_MSG="Failed to create a new ScriptConfiguration. Default parameter repository missing from configuration."
  private static String TOO_MANY_PARAMETERS_ERROR_MSG="${MISSING_PARAMETERS_ERROR_MSG}, not both"

  String name
  String repository
  String command
//  String workspace
//  String project
//  Options options

  String scriptName() {
    (OSUtils.windows) ? WIN_SCRIPT_NAME : SCRIPT_NAME
  }
//  void options(Closure closure) {
//    ConfigureUtil.configure(closure, options)
//  }
  
  private ScriptConfiguration(Builder builder) {
    this.name = (builder?.name) ? builder.name : ''
    this.repository = (builder?.repository) ? builder.repository : ''
    this.command = (builder?.command) ? builder.command : ''
//    this.workspace = (builder?.workspace) ? builder.workspace : ''
//    this.project = (builder?.project) ? builder.project : ''
//    this.options = (builder?.options) ?: new Options()
  }
  
  /**
   * The Builder for the builder construction pattern, includes simple level validation after configuration in the build.
   * @author hallatech
   *
   */
  public static class Builder {
    private String name
    private String repository
    private String command
//    private String workspace
//    private String project
//    private Options options
    public Builder name(String name) {
      this.name = name;
      return this;
    }
    public Builder repository(String repository) {
      this.repository = repository;
      return this;
    }
    public Builder command(String command) {
      this.command = command;
      return this;
    }
//    public Builder workspace(String workspace) {
//      this.workspace = workspace;
//      return this;
//    }
//    public Builder project(String project) {
//      this.project = project;
//      return this;
//    }
//    public Builder options(Options options) {
//      this.options = options;
//      return this;
//    }
    public ScriptConfiguration build() {
      validate()
      return new ScriptConfiguration(this);
    }
    private validate() {
      validateDefaultParameter()
      validateCommandParameter(command)
//      if (!workspace && !project) {
//        throw new IllegalArgumentException(MISSING_PARAMETERS_ERROR_MSG+": \n${new ScriptConfiguration(this).toString()}")
//      }
//      if (workspace && project) {
//        throw new IllegalArgumentException(TOO_MANY_PARAMETERS_ERROR_MSG+": \n${new ScriptConfiguration(this).toString()}")
//      }
    }

    private validateDefaultParameter() {
      if (!repository || !command) {
        throw new IllegalArgumentException(MISSING_DEFAULT_PARAMETERS_ERROR_MSG+": \n${new ScriptConfiguration(this).toString()}")
      }
    }

    private validateCommandParameter(String command) {
      println "Is command=$command in ${VALID_COMMANDS}?"

      if (!(command in VALID_COMMANDS)) {
        throw new IllegalArgumentException(INVALID_COMMAND_ERROR_MSG+": \n${new ScriptConfiguration(this).toString()}")
      }
    }
  }

  /**
   * @return a list of the configured parameters.
   */
  List<String> parameters() {
    List<String> parameters = []
    
    parameters << '-repository'
    parameters << repository

    parameters << "-${command}"

//    if (workspace) {
//      parameters << '-workspace'
//      parameters << workspace
//    }
//    else {
//      parameters << '-project'
//      parameters << project
//    }
//
//    if (options) {
//      options.list().each {
//        parameters << it
//      }
//    }
    
    parameters
  }
  
  /**
   * @return the full command O/S dependent command line and parameters
   */
  List<String> commandLine() {
    List<String> commandLine = []
    
    if (OSUtils.windows) {
      commandLine << 'cmd'
      commandLine << '/c'
      commandLine << WIN_SCRIPT_NAME
    }
    else {
      commandLine << SCRIPT_NAME
    }
    
    parameters().each {
      commandLine << it
    } 
    commandLine
  }
  
  @Override
  public String toString() {
    commandLine().toString()
  }
  
}
