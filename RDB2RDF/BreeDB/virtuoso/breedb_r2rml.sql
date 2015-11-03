--
-- RDF View on relational data in BreeDB using R2RML mapping language.
--
-- Author: Arnold Kuzniar
--

-- clear graphs
SPARQL CLEAR GRAPH <http://temp/germplasm>;
SPARQL CLEAR GRAPH <https://www.eu-sol.wur.nl/passport/>;

-- insert R2RML into a temporary graph
DB.DBA.TTLP('
@prefix rr: <http://www.w3.org/ns/r2rml#> .
@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> .
@prefix gterm: <http://purl.org/germplasm/germplasmTerm#> .
@prefix dwc: <http://rs.tdwg.org/dwc/terms/> .
@prefix dct: <http://purl.org/dc/terms/> .

<#TriplesMap1>
    a rr:TriplesMap;

    rr:logicalTable
    [
      rr:tableSchema "R2RML";
      rr:tableOwner  "TEST";
      rr:tableName   "pp_accession";
    ];

    rr:subjectMap
    [
      rr:template "https://www.eu-sol.wur.nl/passport/{accessionID}";
      rr:class gterm:GermplasmAccession;
      rr:graph <https://www.eu-sol.wur.nl/passport/>;
    ];

    rr:predicateObjectMap
    [
      rr:predicate gterm:germplasmID;
      rr:objectMap [
        rr:template "https://www.eu-sol.wur.nl/passport/SelectAccessionByAccessionID.do?accessionID={accessionID}";
        rr:termType rr:IRI
      ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate gterm:germplasmIdentifier;
      rr:objectMap [ rr:column "accessionName" ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate geo:lat;
      rr:objectMap [ rr:column "gpsLat"; rr:datatype xsd:decimal ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate geo:long;
      rr:objectMap [ rr:column "gpsLong"; rr:datatype xsd:decimal ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate geo:alt;
      rr:objectMap [ rr:column "elevation"; rr:datatype xsd:decimal ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate dwc:countryCode;         # requires two-letter codes in ISO 3166-1-alpha-2
      rr:objectMap [
         rr:column "collectionSiteCountry"; # contains three-letter country codes (FIXME)
         rr:datatype dct:Location           # dct:ISO3166
      ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate dwc:scientificName;
      rr:objectMap [ rr:column "speciesName"; rr:datatype dwc:Taxon ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate gterm:acquisitionDate;
      rr:objectMap [ rr:column "collectionDate"; rr:datatype xsd:date ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate gterm:biologicalStatus;
      rr:objectMap [ rr:column "germplasmStatus"; rr:datatype gterm:BiologicalStatusType ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate dct:modified;
      rr:objectMap [ rr:column "lastUpdate"; rr:datatype xsd:date ];
    ];

    rr:predicateObjectMap
    [
      rr:predicate dct:created;
      rr:objectMap [ rr:column "dateCreated"; rr:datatype xsd:date ];
    ];
.
', 'http://temp/germplasm', 'http://temp/germplasm')
;

-- sanity checks
SELECT DB.DBA.R2RML_TEST('http://temp/germplasm');
DB.DBA.OVL_VALIDATE ('http://temp/germplasm', 'http://www.w3.org/ns/r2rml#OVL');

-- convert R2RML into Virtuoso's own Linked Data Views script
EXEC('SPARQL ' || DB.DBA.R2RML_MAKE_QM_FROM_G('http://temp/germplasm'));

-- graph queries

-- SPARQL SELECT * FROM <http://example.com/germplasm/> WHERE { ?s ?p ?o } LIMIT 10;
-- SPARQL SELECT * FROM <http://example.com/germplasm/> WHERE { ?s ?p ?o . FILTER(datatype(?o) = xsd:decimal) } LIMIT 10;
-- SPARQL DESCRIBE <http://example.com/germplasm/EA00001> FROM <http://example.com/germplasm/>;
-- SPARQL CONSTRUCT { <http://example.com/germplasm/EA00001> ?p ?o } FROM <http://example.com/germplasm/> WHERE { <http://example.com/germplasm/EA00001> ?p ?o };
