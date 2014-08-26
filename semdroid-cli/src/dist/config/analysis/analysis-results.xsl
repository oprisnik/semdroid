<?xml version="1.0" encoding="UTF-8"?>

<!--
  Copyright 2014 Alexander Oprisnik

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  -->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html"
                doctype-system="about:legacy-compat"
                encoding="UTF-8"
                indent="yes"
                version="5.0"/>

    <xsl:template match="/">
<html>
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <meta name="description" content="Semdroid - Semantic Android Application Analysis Using Machine Learning"/>
    <meta name="keywords" content="Android, static analysis, machine learning, semantic patterns, cryptography, SMS, malware"/>
    <meta name="author" content="Alexander Oprisnik"/>
    <link rel="shortcut icon"
          href="../config/analysis/images/semdroid.ico" />

    <title>Semdroid</title>

    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css"/>

    <link rel="stylesheet" href="../config/analysis/css/style.css" />
    <link rel="stylesheet" href="../config/analysis/css/loading.css" />
</head>

<body>

<div id="results-container">
    <div id="results">

        <xsl:for-each select="AnalysisResults">
            <h1><span><a class="btn-back" href="." title="Go back"><i class="glyphicon glyphicon-chevron-left"></i></a></span> Semdroid Analysis Results</h1>

            <div class="container">
                <xsl:for-each select="App">
                    <xsl:variable name="appNr" select="position()"/>
                    <div class="app">
                        <h2>
                            <span class="app-title">application:</span>
                            <span class="app-name"><xsl:value-of select="@name"/></span>
                        </h2>
                    </div>
                    <xsl:for-each select="AppAnalysisReport">
                        <xsl:variable name="reportNr" select="position()"/>
                        <div class="list-group report" id="{concat('report', $reportNr)}">
                            <div class="list-group-item active plugin-name">
                                <xsl:value-of select="@name"/>
                            </div>
                            <xsl:for-each select="Label">
                                <xsl:variable name="labelNr" select="position()"/>
                                <a href="{concat('#app', $appNr,'report', $reportNr, 'label', $labelNr)}" class="list-group-item"  data-toggle="collapse" data-parent="{concat('#report', $reportNr)}">
                                    <span class="badge"><xsl:value-of select="@size"/></span>
                                    <xsl:value-of select="@name"/>
                                </a>
                                <div id="{concat('app', $appNr,'report', $reportNr, 'label', $labelNr)}" class="panel-collapse collapse label-container">
                                    <xsl:for-each select="Labelable">
                                        <div class="labelable">
                                            <xsl:value-of select="."/>
                                        </div>
                                    </xsl:for-each>
                                </div>
                            </xsl:for-each>
                        </div>
                    </xsl:for-each>
                </xsl:for-each>
            </div>
        </xsl:for-each>

            </div>
</div>




<footer class="text-center copyright">
    Copyright &#169; 2014 - Alexander Oprisnik
</footer>

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<script src="../config/analysis/js/semdroid.js"></script>

</body>
</html>
    </xsl:template>

</xsl:stylesheet>
