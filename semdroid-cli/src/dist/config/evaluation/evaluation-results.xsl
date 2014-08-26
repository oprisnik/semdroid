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
                indent="yes"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>Semdroid Evaluation Results</title>

                <link rel="stylesheet" href="../config/evaluation/evaluation-results.css"/>
            </head>
            <body>
                <xsl:for-each select="EvaluationResults">
                    <h1>
                        Semdroid Evaluation:
                        <xsl:value-of select="@name"/>
                    </h1>
                    <h2>Performance:</h2>
                    <table class="Performance">
                        <thead>
                            <tr>
                                <th>Total Instances</th>
                                <th>Correct</th>
                                <th>Correct %</th>
                                <th>Wrong</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>
                                    <!--<xsl:value-of select="Performance/@total"/>-->
                                    <details>
                                        <summary>
                                            <xsl:value-of select="Total/@size"/>
                                        </summary>
                                        <xsl:for-each select="Total/Labelable">
                                            <div class="Labelable">
                                                <xsl:value-of select="."/>
                                            </div>
                                        </xsl:for-each>
                                    </details>
                                </td>
                                <td>
                                    <!-- <xsl:value-of select="Performance/@correct"/>-->
                                    <details>
                                        <summary>
                                            <xsl:value-of select="Correct/@size"/>
                                        </summary>
                                        <xsl:for-each select="Correct/Labelable">
                                            <div class="Labelable">
                                                <xsl:value-of select="."/>
                                            </div>
                                        </xsl:for-each>
                                    </details>
                                </td>
                                <td>
                                    <xsl:value-of select="Performance/@percentageCorrect"/>
                                </td>
                                <td>
                                    <!--<xsl:value-of select="Performance/@wrong"/>-->
                                    <details>
                                        <summary>
                                            <xsl:value-of select="Wrong/@size"/>
                                        </summary>
                                        <xsl:for-each select="Wrong/Labelable">
                                            <div class="Labelable">
                                                <xsl:value-of select="."/>
                                            </div>
                                        </xsl:for-each>
                                    </details>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <br/>
                    <h2>Details:</h2>
                    <table class="EvaluationResults">
                        <thead>
                            <tr>
                                <th>Label</th>
                                <th>Expected</th>
                                <th>Correct</th>
                                <th>Correct %</th>
                                <th>Wrong</th>
                                <th>False Positives</th>
                                <th>False Negatives</th>
                            </tr>
                        </thead>
                        <tbody>
                            <xsl:for-each select="EvaluationResult">
                                <!--<xsl:sort select="rank"/> -->
                                <tr>
                                    <td>
                                        <xsl:value-of select="@name"/>
                                    </td>
                                    <td>
                                        <details>
                                            <summary>
                                                <xsl:value-of select="Expected/@size"/>
                                            </summary>
                                            <xsl:for-each select="Expected/Labelable">
                                                <div class="Labelable">
                                                    <xsl:value-of select="."/>
                                                </div>
                                            </xsl:for-each>
                                        </details>
                                    </td>
                                    <td>
                                        <details>
                                            <summary>
                                                <xsl:value-of select="Correct/@size"/>
                                            </summary>
                                            <xsl:for-each select="Correct/Labelable">
                                                <div class="Labelable">
                                                    <xsl:value-of select="."/>
                                                </div>
                                            </xsl:for-each>
                                        </details>
                                    </td>
                                    <td>
                                        <xsl:value-of select="Performance/@percentageCorrect"/>
                                    </td>
                                    <td>
                                        <details>
                                            <summary>
                                                <xsl:value-of select="Wrong/@size"/>
                                            </summary>
                                            <xsl:for-each select="Wrong/Labelable">
                                                <div class="Labelable">
                                                    <xsl:value-of select="."/>
                                                </div>
                                            </xsl:for-each>
                                        </details>
                                    </td>
                                    <td>
                                        <details>
                                            <summary>
                                                <xsl:value-of select="FalsePositives/@size"/>
                                            </summary>
                                            <xsl:for-each select="FalsePositives/Labelable">
                                                <div class="Labelable">
                                                    <xsl:value-of select="."/>
                                                </div>
                                            </xsl:for-each>
                                        </details>
                                    </td>
                                    <td>
                                        <details>
                                            <summary>
                                                <xsl:value-of select="FalseNegatives/@size"/>
                                            </summary>
                                            <xsl:for-each select="FalseNegatives/Labelable">
                                                <div class="Labelable">
                                                    <xsl:value-of select="."/>
                                                </div>
                                            </xsl:for-each>
                                        </details>
                                    </td>
                                </tr>
                            </xsl:for-each>
                        </tbody>
                    </table>

                </xsl:for-each>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>