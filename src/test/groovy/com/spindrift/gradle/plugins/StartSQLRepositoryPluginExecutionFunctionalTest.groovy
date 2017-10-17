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
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class StartSQLRepositoryPluginExecutionFunctionalTest extends Specification {

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
    result.output.contains("ARGS: args = -repository /atg/userprofiling/ProfileAdapterRepository -outputSQL")
    result.task(":startSQLRepository").outcome == SUCCESS
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

  def "startSQLRepository task invoked with server option"() {
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
              server = 'original'
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
    result.output.contains("ARGS: args = -m DCS -s original -repository /atg/userprofiling/ProfileAdapterRepository -outputSQL")
    result.output.contains("drop table dcs_")
    result.output.contains("CREATE TABLE dcs_")
    result.task(":startSQLRepository").outcome == SUCCESS
  }

  def "startSQLRepository task invoked with exportAll option"() {
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
              command = 'exportAll'
              modules = ['DCS']
              file = '/tmp/profile_data_all.xml'
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
    result.output.contains("ARGS: args = -m DCS -repository /atg/userprofiling/ProfileAdapterRepository -export all /tmp/profile_data_all.xml")
    result.task(":startSQLRepository").outcome == SUCCESS
  }

  def "startSQLRepository task invoked with export itemTypes option"() {
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
              command = 'export'
              modules = ['DCS']
              file = '/tmp/profile_data_all.xml'
              itemTypes = ['user','organization']
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
    result.output.contains("ARGS: args = -m DCS -repository /atg/userprofiling/ProfileAdapterRepository -export user,organization /tmp/profile_data_all.xml")
    result.task(":startSQLRepository").outcome == SUCCESS
  }



}
