# pdftimestamp

pdftimestamp add a document timestamp signature for a PDF file.

Most of source code are copied from PDFBox examples.
Basically, this software combines two PDFBox examples
(CreateSignedTimeStamp and AddValidationInformation) and
add password option for encryption.

CreateSignedTimeStamp signs PAdES (PDF Advanced Electronic Signatures).
AddValidationInformation adds validation information for PAdES-LTV.

## Author

Tanaka Akira <akr@fsij.org>

## Prerequisite

- Apache Maven

## Build

    mvn clean package

## Run

    mvn exec:java -Dexec.mainClass="org.fsij.pdftimestamp.PDFTimeStamp" \
      -Dexec.args="[-p PDF-PASSWORD] TSA-URL INPUT-PDF OUTPUT-PDF"

If you want to run without Maven, set CLASSPATH with
`mvn dependency:build-classpath` and `target/pdftimestamp-*.jar`.

## Links

- https://github.com/akr/pdftimestamp

## License

Apache License Version 2.0
