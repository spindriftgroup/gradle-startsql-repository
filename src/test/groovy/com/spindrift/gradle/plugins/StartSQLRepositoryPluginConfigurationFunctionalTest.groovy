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

import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import org.gradle.testkit.runner.UnexpectedBuildFailure

class StartSQLRepositoryPluginConfigurationFunctionalTest extends Specification {

  @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
  File buildFile

  def setup() {
    buildFile = testProjectDir.newFile('build.gradle')
  }

  def "startSQLRepository task invoked with invalid command"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'wrongCommand'
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository','-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("Invalid command specified. Use one of [outputSQL, outputSQLFile, import, export, exportAll, exportRepositories]")
  }

  def "startSQLRepository task invoked with invalid named configuration"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              name = 'outputProfileSQL'
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'outputSQL'
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository','-PconfigName=someInvalidName','-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("startSQLRepository configuration with [name=someInvalidName] not found.")
  }

  def "startSQLRepository task invoked with invalid named configuration where no names specified"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'outputSQL'
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository','-PconfigName=someName','-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("startSQLRepository configuration with [name=someName] not found.")
  }

  def "startSQLRepository task invoked with invalid named configuration where multiple names specified"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              name = 'outputProfileSql'
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'outputSQL'
            }
            parameters {
              name = 'outputCatalogSql'
              repository = "/atg/commerce/catalog/ProductCatalog"
              command = 'outputSQL'
            }
            parameters {
              name = 'outputSiteSql'
              repository = "/atg/multisite/SiteRepository"
              command = 'outputSQL'
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository','-PconfigName=outputSomeSql','-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("startSQLRepository configuration with [name=outputSomeSql] not found.")
  }

  def "invoke showConfigurations task with named and un-named configuration"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              name = 'outputProfileSql'
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'outputSQL'
              modules = ['DCS']
            }
            parameters {
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'outputSQL'
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('showConfigurations','-s')
        .withPluginClasspath()
        .build()

    then:
    result.output.contains("startSQLRepository configurations")
    result.output.contains("name: outputProfileSql")
    result.output.contains("repository: /atg/userprofiling/ProfileAdapterRepository")
    result.output.contains("modules: [DCS]")
    result.output.contains("un-named: 1")
    result.task(":showConfigurations").outcome == SUCCESS
  }

  def "invoke showConfigurations task and show output CLI arguments"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              name = 'outputProfileSql'
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'outputSQL'
              modules = ['DCS']
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('showConfigurations','-s')
        .withPluginClasspath()
        .build()

    then:
    result.output.contains("startSQLRepository configurations")
    result.output.contains("name: outputProfileSql")
    result.output.contains("repository: /atg/userprofiling/ProfileAdapterRepository")
    result.output.contains("modules: [DCS]")
    result.output.contains("CLI args: startSQLRepository -m DCS -repository /atg/userprofiling/ProfileAdapterRepository -outputSQL")
    result.task(":showConfigurations").outcome == SUCCESS
  }

  def "startSQLRepository task invoked with outputSQLFile command where no file name specified"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'outputSQLFile'
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository','-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("Failed to create a new ScriptConfiguration. Required parameter(s) [file] missing from configuration.")
  }

  def "startSQLRepository task invoked with import command where no file name specified"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'import'
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository', '-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("Failed to create a new ScriptConfiguration. Required parameter(s) [file] missing from configuration.")
  }

  def "startSQLRepository task invoked with export command where no file name specified"() {
    given:
    buildFile << """
      plugins {
        id 'com.spindrift.startsql-repository'
      }

      startSQLRepository {
        configurations {
          parameters {
            repository = "/atg/userprofiling/ProfileAdapterRepository"
            command = 'export'
          }
        }
      }
    """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository', '-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("Failed to create a new ScriptConfiguration. Required parameter(s) [file] missing from configuration.")
  }

  def "startSQLRepository task invoked with exportAll command where no file name specified"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'export'
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository', '-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("Failed to create a new ScriptConfiguration. Required parameter(s) [file] missing from configuration.")
  }

  def "startSQLRepository task invoked with export command where no item types specified"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'export'
              file = '/tmp/profile-data-select-type.xml'
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository', '-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("Failed to create a new ScriptConfiguration. Required parameter(s) [itemTypes] missing from configuration.")
  }

  def "startSQLRepository task invoked with exportRepositories command where no repositories specified"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              command = 'exportRepositories'
              file = '/tmp/profile-data-select-type.xml'
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository', '-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("Failed to create a new ScriptConfiguration. Required parameter(s) [repositories] missing from configuration.")
  }

  def "startSQLRepository task invoked with invalid extra options"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              name = 'outputProfileSQL'
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'outputSQL'
              options {
                invalid = 'value'
              }
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository','-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("Could not set unknown property 'invalid' for object of type com.spindrift.gradle.config.Options.")
  }

  def "startSQLRepository task invoked with invalid database option"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              name = 'outputProfileSQL'
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'outputSQL'
              options {
                database = 'aurora'
              }
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository','-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("The option value for [database=aurora] must be one of [oracle, sybase, solid, informix, db2, mysql].")
  }

  def "startSQLRepository task invoked with import options when command is not import"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              name = 'outputProfileSQL'
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'outputSQL'
              file = 'profile_data.xml'
              options {
                project = 'myproject'
                user = 'me'
              }
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository','-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    def m = "The option values [workflow, activity, project, workspace, user, comment] " +
        "can only be used with the import command. " +
        "The [command=outputSQL] is invalid in this configuration."
    t.message.contains(m)
  }

  def "startSQLRepository task invoked with import requires user option if project is specified"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              name = 'outputProfileSQL'
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'import'
              file = 'profile_data.xml'
              options {
                project = 'myproject'
              }
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository','-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("The option value [user=<username>] is required if the project option is specified.")
  }

  def "startSQLRepository task invoked with non-export command skipReferences option invalidly specified"() {
    given:
    buildFile << """
        plugins {
          id 'com.spindrift.startsql-repository'
        }

        startSQLRepository {
          configurations {
            parameters {
              name = 'outputProfileSQL'
              repository = "/atg/userprofiling/ProfileAdapterRepository"
              command = 'import'
              file = 'profile_data.xml'
              options {
                skipReferences true
              }
            }
          }
        }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir.root)
        .withArguments('startSQLRepository','-s')
        .withPluginClasspath()
        .build()

    then:
    def t = thrown(UnexpectedBuildFailure)
    t.message.contains("The option value [skipReferences] is only valid for "
        .concat("[export, exportAll, exportRepositories] commands."))
  }
}
