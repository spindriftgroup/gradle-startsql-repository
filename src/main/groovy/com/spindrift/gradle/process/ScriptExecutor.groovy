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
package com.spindrift.gradle.process

import com.spindrift.gradle.config.ScriptConfiguration;

import static org.gradle.api.logging.Logging.getLogger

class ScriptExecutor implements Executor {
  private static final String LOGGER='system.out'
  
  ScriptConfiguration scriptConfiguration
  
  @Override
  ExecutionResult execute(ScriptConfiguration script) {
    scriptConfiguration = script
    Process process = scriptConfiguration.commandLine().execute()
    handleProcessExecution(process)
  }
  
  private handleProcessExecution(Process process) {
    int exitVal
    try {
      StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR")
      StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT")

      errorGobbler.start()
      outputGobbler.start()

      exitVal = process.waitFor();
      getLogger(LOGGER).debug "ExitValue: $exitVal"
      if (exitVal != 0) {
        getLogger(LOGGER).error """[ERROR] Script execution failed with code:${exitVal} 
          and script parameters:${scriptConfiguration.commandLine()}"""
      }

    } catch (Throwable t) {
      t.printStackTrace()
    }

    new ExecutionResult(exitValue:exitVal, output:process.in.text, errorText:process.err.text)
  }


  class StreamGobbler extends Thread {
    InputStream is
    String streamType

    StreamGobbler(InputStream is, String streamType) {
      this.is = is
      this.streamType = streamType
    }

    void run() {
      try {
        InputStreamReader isr = new InputStreamReader(is)
        BufferedReader br = new BufferedReader(isr)
        String line=null
        while ((line = br.readLine()) != null)
          println(line)
      }
      catch (IOException ioe) {
        ioe.printStackTrace()
      }
    }
  }

}
