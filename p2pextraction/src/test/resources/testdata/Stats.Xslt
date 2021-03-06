<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output omit-xml-declaration="yes"/>

    <xsl:variable name="training-file" select="'PMtask_Relations_TrainingSet.xml'"/>

    <xsl:variable name="training-doc" select="document($training-file)"/>

<xsl:template match="node() | @*">

    <xsl:apply-templates select="@*|node()"/>
</xsl:template>

    <xsl:template match="collection">
        <tr>
        <td>Gene name count</td>
        <td> <xsl:value-of select="count( distinct-values($training-doc//annotation[infon[@key='type']/text()='Gene']/infon[@key='NCBI GENE']/text()))"/>
        </td>

        <td> <xsl:value-of select="count(//annotation[infon[@key='type']/text()='Gene'])"/>
        </td>



    </tr>
        <tr>
            <td>Unique gene Id  count</td>
            <td> <xsl:value-of select="count(distinct-values( $training-doc//annotation[infon[@key='type']/text()='Gene']))"/>
            </td>

            <td> <xsl:value-of select="count(distinct-values( //annotation[infon[@key='type']/text()='Gene']))"/>
            </td>



        </tr>

        <tr>
            <td>Document count </td>
            <td> <xsl:value-of select="count($training-doc//document)"/>
            </td>

            <td> <xsl:value-of select="count(//document)"/>
            </td>



        </tr>
        <tr>
            <td>Relation count </td>
            <td> <xsl:value-of select="count($training-doc//relation)"/>
            </td>

            <td> <xsl:value-of select="count(//relation)"/>
            </td>



        </tr>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:template>

    <!--<xsl:template match="annotation[infon[@key='type']/text()='Gene']">-->

                <!--<xsl:variable name="geneName" select="text/text()"/>-->
                <!--<xsl:variable name="docId" select="../../id"/>-->
        <!--<xsl:value-of select="'*********'"/>-->
        <!--&lt;!&ndash;<xsl:value-of select="$docId"/>&ndash;&gt;-->
        <!--&lt;!&ndash;<xsl:value-of select="$geneName"/>&ndash;&gt;-->
        <!--<br/>-->
        <!--<xsl:value-of select="$training-doc//document[id=$docId]//annotation[infon[@key='type']/text()='Gene'][text/text()=$geneName]/text/text()"/>-->
                <!--&lt;!&ndash;<xsl:value-of select="$training-doc//document[id=$docId]//annotation[infon[infon[@key='type']/text()='Gene']/text/text()=$geneName]"/>&ndash;&gt;-->


    <!--</xsl:template>-->

</xsl:stylesheet>