/*
 * Copyright 2016 Crown Copyright
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

package gaffer.example.gettingstarted.analytic;

import gaffer.commonutil.iterable.CloseableIterable;
import gaffer.data.element.Edge;
import gaffer.data.element.Element;
import gaffer.data.element.function.ElementTransformer;
import gaffer.data.elementdefinition.view.View;
import gaffer.data.elementdefinition.view.ViewElementDefinition;
import gaffer.example.gettingstarted.function.transform.MeanTransform;
import gaffer.example.gettingstarted.generator.DataGenerator4;
import gaffer.example.gettingstarted.util.DataUtils;
import gaffer.graph.Graph;
import gaffer.operation.OperationException;
import gaffer.operation.data.EntitySeed;
import gaffer.operation.impl.add.AddElements;
import gaffer.operation.impl.get.GetRelatedEdges;
import gaffer.user.User;
import java.util.ArrayList;
import java.util.List;

public class LoadAndQuery4 extends LoadAndQuery {
    public LoadAndQuery4() {
        super("Transforms");
    }

    public static void main(final String[] args) throws OperationException {
        new LoadAndQuery4().run();
    }

    public CloseableIterable<Edge> run() throws OperationException {
        final User user = new User("user01");

        //create some edges from the data file using our data generator class
        final List<Element> elements = new ArrayList<>();
        final DataGenerator4 dataGenerator = new DataGenerator4();
        for (String s : DataUtils.loadData(getData())) {
            elements.add(dataGenerator.getElement(s));
        }
        log("Elements generated from the data file.");
        for (final Element element : elements) {
            log("GENERATED_EDGES", element.toString());
        }
        log("");

        //create a graph using our schema and store properties
        final Graph graph = new Graph.Builder()
                .addSchemas(getSchemas())
                .storeProperties(getStoreProperties())
                .build();

        //add the edges to the graph
        final AddElements addElements = new AddElements.Builder()
                .elements(elements)
                .build();
        graph.execute(addElements, user);
        log("The elements have been added.\n");

        //get all the edges that contain the vertex "1"
        final GetRelatedEdges<EntitySeed> getRelatedEdges = new GetRelatedEdges.Builder<EntitySeed>()
                .addSeed(new EntitySeed("1"))
                .build();
        final CloseableIterable<Edge> results = graph.execute(getRelatedEdges, user);
        log("\nAll edges containing the vertex 1. The counts and 'things' have been aggregated\n");
        for (Element e : results) {
            log("GET_RELATED_EDGES_RESULT", e.toString());
        }

        //rerun previous query but calculate a mean
        // Create a mean transient property using an element transformer
        final ElementTransformer mean = new ElementTransformer.Builder()
                .select("thing", "count")
                .project("mean")
                .execute(new MeanTransform())
                .build();

        // Add the element transformer to the view
        final View view = new View.Builder()
                .edge("data", new ViewElementDefinition.Builder()
                        .transientProperty("mean", Float.class)
                        .transformer(mean)
                        .build())
                .build();

        final GetRelatedEdges<EntitySeed> getRelatedEdgesWithMean = new GetRelatedEdges.Builder<EntitySeed>()
                .addSeed(new EntitySeed("1"))
                .view(view)
                .build();
        final CloseableIterable<Edge> transientResults = graph.execute(getRelatedEdgesWithMean, user);
        log("\nWe can add a new property to the edges that is calculated from the aggregated values of other properties\n");
        for (Element e : transientResults) {
            log("GET_RELATED_ELEMENTS_WITH_MEAN_RESULT", e.toString());
        }

        return transientResults;
    }
}
