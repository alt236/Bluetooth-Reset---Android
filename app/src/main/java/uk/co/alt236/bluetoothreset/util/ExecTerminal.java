/**
 * ****************************************************************************
 * Copyright 2011 Alexandros Schillings
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */
package uk.co.alt236.bluetoothreset.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecTerminal {
    private final String TAG = this.getClass().getName();

    public class ExecResult {
        private final String stdOut;
        private final String stdErr;
        private final String stdIn;
        private final int exitCode;

        public ExecResult(final String stdIn,
                          final int exitCode,
                          final String stdOut,
                          final String stdErr) {

            this.stdOut = stdOut;
            this.stdErr = stdErr;
            this.stdIn = stdIn;
            this.exitCode = exitCode;
        }

        public ExecResult() {
            stdOut = "";
            stdErr = "";
            stdIn = "";
            exitCode = Integer.MIN_VALUE;
        }

        public String getStdOut() {
            return stdOut;
        }

        public String getStdErr() {
            return stdErr;
        }

        public String getStdIn() {
            return stdIn;
        }

        public int getExitCode() {
            return exitCode;
        }
    }

    public boolean checkSu() {
        final ExecTerminal et = new ExecTerminal();
        ExecResult res = et.execSu("su && echo 1");

        if (res.getStdOut().trim().equals("1")) {
            Log.i(TAG, "^ got root!");
            return true;
        }

        Log.w(TAG, "^ could not get root.");
        return false;
    }

    public ExecResult exec(String cmd) {
        Log.d(TAG, "^ exec(): '" + cmd + "'");
        String stdOut = "";
        String stdErr = "";
        int exitCode = Integer.MIN_VALUE;

        try {
            final Process process = Runtime.getRuntime().exec("sh");

            final DataInputStream is = new DataInputStream(process.getInputStream());
            final DataInputStream eis = new DataInputStream(process.getErrorStream());
            final DataOutputStream os = new DataOutputStream(process.getOutputStream());

            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();

            final BufferedReader readerOut = new BufferedReader(new InputStreamReader(is));
            final BufferedReader readerErr = new BufferedReader(new InputStreamReader(eis));

            exitCode = process.waitFor();

            try {
                String line = "";
                while ((line = readerOut.readLine()) != null) {
                    stdOut = stdOut + line + "\n";
                }
                readerOut.close();
            } catch (IOException e) {
                Log.e(TAG, "^ exec() - IOException in sdtdOut Loop: " + e.getMessage());
            }


            try {
                String line = "";
                while ((line = readerErr.readLine()) != null) {
                    stdErr = stdErr + line + "\n";
                }
                readerErr.close();
            } catch (IOException e) {
                Log.e(TAG, "^ exec() - IOException in stdErr Loop: " + e.getMessage());
            }

        } catch (IOException e) {
            Log.e(TAG, "^ exec() IOException: " + e.getMessage());

        } catch (InterruptedException e) {
            Log.e(TAG, "^ exec() InterruptedException: " + e.getMessage());
        }

        return new ExecResult(cmd, exitCode, stdOut, stdErr);
    }

    public ExecResult execSu(String cmd) {
        Log.d(TAG, "^ execSu(): '" + cmd + "'");
        String stdOut = "";
        String stdErr = "";
        int exitCode = Integer.MIN_VALUE;

        try {
            final Process process = Runtime.getRuntime().exec("su");
            final DataInputStream is = new DataInputStream(process.getInputStream());
            final DataInputStream eis = new DataInputStream(process.getErrorStream());
            final DataOutputStream os = new DataOutputStream(process.getOutputStream());

            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();

            final BufferedReader readerOut = new BufferedReader(new InputStreamReader(is));
            final BufferedReader readerErr = new BufferedReader(new InputStreamReader(eis));

            exitCode = process.waitFor();

            try {
                String line = "";
                while ((line = readerOut.readLine()) != null) {
                    stdOut = stdOut + line + "\n";
                }
                readerOut.close();
            } catch (IOException e) {
                Log.e(TAG, "^ execSu() - IOException in sdtdOut Loop: " + e.getMessage());
            }

            try {
                String line = "";
                while ((line = readerErr.readLine()) != null) {
                    stdErr = stdErr + line + "\n";
                }
                readerErr.close();
            } catch (IOException e) {
                Log.e(TAG, "^ execSu() - IOException in sdtdOut Loop: " + e.getMessage());
            }

        } catch (IOException e) {
            Log.e(TAG, "^ execSu() - IOException: " + e.getMessage());
        } catch (InterruptedException e) {
            Log.e(TAG, "^ execSu() - InterruptedException: " + e.getMessage());
        }

        return new ExecResult(cmd, exitCode, stdOut, stdErr);
    }
}
