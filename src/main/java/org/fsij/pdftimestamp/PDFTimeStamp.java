/* Copyright 2019 Tanaka Akira
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fsij.pdftimestamp;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.*;
import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.encryption.*;
import java.io.*;
import java.util.Calendar;
import java.security.*;
import java.net.*;
import org.apache.log4j.*;

/*
 * Usage:
 *   pdftimestamp [-p PDF-PASSWORD] TSA-URL INPUT-PDF OUTPUT-PDF
 */

public class PDFTimeStamp {
  public static void main(String[] args){

    BasicConfigurator.configure();

    int n = 0;
    String password = null;

    if (args[n].equals("-p")) {
      password = args[n+1];
      n += 2;
    }

    String tsaURL;
    tsaURL = args[n];

    n++;

    File inputFile = new File(args[n]);
    File tempFile = new File(args[n+1] + ".tmp");
    File outputFile = new File(args[n+1]);

    addTimestamp(tsaURL, inputFile, tempFile, password);
    addLTV(tempFile, outputFile, password);

    tempFile.delete();
  }

  private static void addTimestamp(String tsaURL, File inputFile, File outputFile, String password) {
    CreateSignedTimeStamp signing = new CreateSignedTimeStamp(tsaURL);
    try { signing.signDetached(inputFile, outputFile, password); }
    catch (IOException e) { System.out.println("signing.signDetached"); System.exit(1); return; }
  }

  private static void addLTV(File inputFile, File outputFile, String password) {
    try { Security.addProvider(SecurityProvider.getProvider()); }
    catch (IOException e) { System.out.println("Security.addProvider"); System.exit(1); return; }
    AddValidationInformation addOcspInformation = new AddValidationInformation();
    try { addOcspInformation.validateSignature(inputFile, outputFile, password); }
    catch (IOException e) { System.out.println("addOcspInformation.validateSignature"); System.exit(1); return; }

  }
}
