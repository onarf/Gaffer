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
package gaffer.example.operation;

import gaffer.commonutil.iterable.CloseableIterable;
import gaffer.data.element.function.ElementFilter;
import gaffer.data.elementdefinition.view.View;
import gaffer.data.elementdefinition.view.ViewElementDefinition;
import gaffer.function.simple.filter.IsMoreThan;
import gaffer.operation.GetOperation.IncludeIncomingOutgoingType;
import gaffer.operation.data.EntitySeed;
import gaffer.operation.impl.get.GetAdjacentEntitySeeds;

public class GetAdjacentEntitySeedsExample extends OperationExample {
    public static void main(final String[] args) {
        new GetAdjacentEntitySeedsExample().run();
    }

    public GetAdjacentEntitySeedsExample() {
        super(GetAdjacentEntitySeeds.class);
    }

    public void runExamples() {
        getAdjacentEntitySeedsFromVertex2();
        getAdjacentEntitySeedsAlongOutboundEdgesFromVertex2();
        getAdjacentEntitySeedsAlongOutboundEdgesFromVertex2WithCountGreaterThan1();
    }

    public CloseableIterable<EntitySeed> getAdjacentEntitySeedsFromVertex2() {
        final String opJava = "new GetAdjacentEntitySeeds.Builder()\n"
                + "                .addSeed(new EntitySeed(2))\n"
                + "                .build();";
        return runExample(new GetAdjacentEntitySeeds.Builder()
                .addSeed(new EntitySeed(2))
                .build(), opJava);
    }

    public CloseableIterable<EntitySeed> getAdjacentEntitySeedsAlongOutboundEdgesFromVertex2() {
        final String opJava = "new GetAdjacentEntitySeeds.Builder()\n"
                + "                .addSeed(new EntitySeed(2))\n"
                + "                .inOutType(IncludeIncomingOutgoingType.OUTGOING)\n"
                + "                .build();";
        return runExample(new GetAdjacentEntitySeeds.Builder()
                .addSeed(new EntitySeed(2))
                .inOutType(IncludeIncomingOutgoingType.OUTGOING)
                .build(), opJava);
    }

    public CloseableIterable<EntitySeed> getAdjacentEntitySeedsAlongOutboundEdgesFromVertex2WithCountGreaterThan1() {
        final String opJava = "new GetAdjacentEntitySeeds.Builder()\n"
                + "                .addSeed(new EntitySeed(2))\n"
                + "                .inOutType(IncludeIncomingOutgoingType.OUTGOING)\n"
                + "                .view(new View.Builder()\n"
                + "                        .entity(\"entity\", new ViewElementDefinition.Builder()\n"
                + "                                .filter(new ElementFilter.Builder()\n"
                + "                                        .select(\"count\")\n"
                + "                                        .execute(new IsMoreThan(1))\n"
                + "                                        .build())\n"
                + "                                .build())\n"
                + "                        .edge(\"edge\", new ViewElementDefinition.Builder()\n"
                + "                                .filter(new ElementFilter.Builder()\n"
                + "                                        .select(\"count\")\n"
                + "                                        .execute(new IsMoreThan(1))\n"
                + "                                        .build())\n"
                + "                                .build())\n"
                + "                        .build())\n"
                + "                .build();";
        return runExample(new GetAdjacentEntitySeeds.Builder()
                .addSeed(new EntitySeed(2))
                .inOutType(IncludeIncomingOutgoingType.OUTGOING)
                .view(new View.Builder()
                        .entity("entity", new ViewElementDefinition.Builder()
                                .preAggregationFilter(new ElementFilter.Builder()
                                        .select("count")
                                        .execute(new IsMoreThan(1))
                                        .build())
                                .build())
                        .edge("edge", new ViewElementDefinition.Builder()
                                .preAggregationFilter(new ElementFilter.Builder()
                                        .select("count")
                                        .execute(new IsMoreThan(1))
                                        .build())
                                .build())
                        .build())
                .build(), opJava);
    }
}
