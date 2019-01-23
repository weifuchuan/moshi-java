package com.moshi.service;

import cn.hutool.core.io.file.FileReader;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.jboot.Jboot;


import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class GraphQLGetterImpl implements GraphQLGetter {
  private GraphQL graph;

  public GraphQLGetterImpl() {
    String schema = new FileReader(Jboot.configValue("graphql.schema")).readString();
    SchemaParser schemaParser = new SchemaParser();
    TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);
    SchemaGenerator schemaGenerator = new SchemaGenerator();
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, buildRuntimeWiring());
    graph = GraphQL.newGraphQL(graphQLSchema).build();
  }

  RuntimeWiring buildRuntimeWiring() {
    return RuntimeWiring.newRuntimeWiring()

        .build();
  }

  public GraphQL getGraph() {
    return graph;
  }
}
