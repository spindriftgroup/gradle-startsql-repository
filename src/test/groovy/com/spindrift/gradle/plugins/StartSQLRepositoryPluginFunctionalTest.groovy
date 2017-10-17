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

class StartSQLRepositoryPluginFunctionalTest extends Specification {

  @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
  File buildFile

  def setup() {
    buildFile = testProjectDir.newFile('build.gradle')
  }

  def "startSQLRepository task invokes startSQLRepository utility"() {
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
        .withArguments('startSQLRepository','-s')
        .withPluginClasspath()
        .build()

    then:
    result.output.contains("Executing: startSQLRepository")
    result.task(":startSQLRepository").outcome == SUCCESS
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
    t.message.contains("Invalid command specified. Use one of [outputSQL, outputSQLFile, import, export, exportRepositories]")
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

  def "startSQLRepository task invoked with modules option"() {
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
        .withArguments('startSQLRepository','-i','-s')
        .withPluginClasspath()
        .build()

    then:
    result.output.contains("drop table dcs_")
    result.output.contains("CREATE TABLE dcs_")
    result.task(":startSQLRepository").outcome == SUCCESS
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
    result.output.contains("startSQLRepository configurations") &&
        result.output.contains("Name: outputProfileSql") &&
        result.output.contains("Repository: /atg/userprofiling/ProfileAdapterRepository") &&
        result.output.contains("Modules: [DCS]") &&
        result.output.contains("UnNamed: 1") &&
    result.task(":showConfigurations").outcome == SUCCESS
  }


}
