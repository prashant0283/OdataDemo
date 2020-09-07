package com.test.odataadapter.service;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.ex.ODataException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.olingo.server.api.serializer.Kind.EntityType;
import static org.apache.olingo.server.api.serializer.Kind.NavigationProperty;

@Component
public class CarsEdmProvider extends CsdlAbstractEdmProvider{

    // Service Namespace
    public static final String NAMESPACE = "olingo.odata.sample";

    // EDM Container
    public static final String CONTAINER_NAME = "Container";
    public static final FullQualifiedName CONTAINER_FQN = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

    // Entity Types Names
    public static final FullQualifiedName ET_CAR = new FullQualifiedName(NAMESPACE, "Car");
    public static final FullQualifiedName ET_MANUFACTURER = new FullQualifiedName(NAMESPACE, "Manufacturer");

    // Complex Type Names
    public static final FullQualifiedName CT_ADDRESS = new FullQualifiedName(NAMESPACE, "Address");

    // Entity Set Names
    public static final String ES_CARS_NAME = "Cars";
    public static final String ES_MANUFACTURER_NAME = "Manufacturers";


    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {
        List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(NAMESPACE);

        // EntityTypes
        List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
        entityTypes.add(getEntityType(ET_CAR));
        entityTypes.add(getEntityType(ET_MANUFACTURER));
        schema.setEntityTypes(entityTypes);

        // ComplexTypes
        List<CsdlComplexType> complexTypes = new ArrayList<>();
        complexTypes.add(getComplexType(CT_ADDRESS));
        schema.setComplexTypes(complexTypes);

        // EntityContainer
        schema.setEntityContainer(getEntityContainer());
        schemas.add(schema);

        return schemas;
    }

    @Override
    public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
        if (CT_ADDRESS.equals(complexTypeName)) {
            return new CsdlComplexType()
                    .setName(CT_ADDRESS.getName())
                    .setProperties(Arrays.asList(
                            new CsdlProperty().setName("Street").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                            new CsdlProperty().setName("City").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                            new CsdlProperty().setName("ZipCode").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                            new CsdlProperty().setName("Country").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())));
        }
        return null;
    }

    @Override
    public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
        if (ET_CAR.equals(entityTypeName)) {
            return new CsdlEntityType()
                    .setName(ET_CAR.getName())
                    .setKey(Arrays.asList(
                            new CsdlPropertyRef().setName("Id")))
                    .setProperties(Arrays.asList(
                            new CsdlProperty().setName("Id").setType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName()),
                            new CsdlProperty().setName("Model").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                            new CsdlProperty().setName("ModelYear").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                                    .setMaxLength(4),
                            new CsdlProperty().setName("Price").setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName())
                                    .setScale(2),
                            new CsdlProperty().setName("Currency").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                                    .setMaxLength(3)
                    ))
                    .setNavigationProperties(Arrays.asList(
                            new CsdlNavigationProperty().setName("Manufacturer").setType(ET_MANUFACTURER)
                    ));

        } else if (ET_MANUFACTURER.equals(entityTypeName)) {
            return new CsdlEntityType()
                    .setName(ET_MANUFACTURER.getName())
                    .setKey(Arrays.asList(new CsdlPropertyRef().setName("Id")))
                    .setProperties(Arrays.asList(
                            new CsdlProperty().setName("Id").setType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName()),
                            new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                            new CsdlProperty().setName("Address").setType(CT_ADDRESS))
                    )
                    .setNavigationProperties(Arrays.asList(
                            new CsdlNavigationProperty().setName("Cars").setType(ET_CAR).setCollection(true)
                    ));
        }

        return null;
    }

    @Override
    public CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName) throws ODataException {
        if (CONTAINER_FQN.equals(entityContainer)) {
            if (ES_CARS_NAME.equals(entitySetName)) {
                return new CsdlEntitySet()
                        .setName(ES_CARS_NAME)
                        .setType(ET_CAR)
                        .setNavigationPropertyBindings(Arrays.asList(
                                new CsdlNavigationPropertyBinding()
                                        .setPath("Manufacturer")
                                        .setTarget(ES_MANUFACTURER_NAME)
                               )
                        );
            } else if (ES_MANUFACTURER_NAME.equals(entitySetName)) {
                return new CsdlEntitySet()
                        .setName(ES_MANUFACTURER_NAME)
                        .setType(ET_MANUFACTURER).setNavigationPropertyBindings(
                                Arrays.asList(
                                        new CsdlNavigationPropertyBinding()
                                                .setPath("Cars")
                                                .setTarget(ES_CARS_NAME)
                                )
                        );
            }
        }

        return null;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() throws ODataException {
        CsdlEntityContainer container = new CsdlEntityContainer();
        container.setName(CONTAINER_FQN.getName());

        // EntitySets
        List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
        container.setEntitySets(entitySets);
        entitySets.add(getEntitySet(CONTAINER_FQN, ES_CARS_NAME));
        entitySets.add(getEntitySet(CONTAINER_FQN, ES_MANUFACTURER_NAME));

        return container;
    }


    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName) throws ODataException {
        if (entityContainerName == null || CONTAINER_FQN.equals(entityContainerName)) {
            return new CsdlEntityContainerInfo().setContainerName(CONTAINER_FQN);
        }
        return null;
    }




}
