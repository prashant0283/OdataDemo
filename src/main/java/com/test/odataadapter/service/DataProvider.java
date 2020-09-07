package com.test.odataadapter.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.server.api.uri.UriParameter;

public class DataProvider {

    private Map<String, EntityCollection> data;

    public DataProvider() {
        data = new HashMap<String, EntityCollection>();
        data.put("Cars", createCars());
        data.put("Manufacturers", createManufacturers());
    }

    private EntityCollection createCars() {
        EntityCollection entitySet = new EntityCollection();

        entitySet.getEntities().add(new Entity()
                .addProperty(createPrimitive("Id", 1))
                .addProperty(createPrimitive("Model", "F1 W03"))
                .addProperty(createPrimitive("ModelYear", "2012"))
                .addProperty(createPrimitive("Price", 189189.43))
                .addProperty(createPrimitive("Currency", "EUR")));

        entitySet.getEntities().add(new Entity()
                .addProperty(createPrimitive("Id", 2))
                .addProperty(createPrimitive("Model", "F1 W04"))
                .addProperty(createPrimitive("ModelYear", "2013"))
                .addProperty(createPrimitive("Price", 199999.99))
                .addProperty(createPrimitive("Currency", "EUR")));

        entitySet.getEntities().add(new Entity()
                .addProperty(createPrimitive("Id", 3))
                .addProperty(createPrimitive("Model", "F2012"))
                .addProperty(createPrimitive("ModelYear", "2012"))
                .addProperty(createPrimitive("Price", 137285.33))
                .addProperty(createPrimitive("Currency", "EUR")));

        entitySet.getEntities().add(new Entity()
                .addProperty(createPrimitive("Id", 4))
                .addProperty(createPrimitive("Model", "F2013"))
                .addProperty(createPrimitive("ModelYear", "2013"))
                .addProperty(createPrimitive("Price", 145285.00))
                .addProperty(createPrimitive("Currency", "EUR")));

        entitySet.getEntities().add(new Entity()
                .addProperty(createPrimitive("Id", 5))
                .addProperty(createPrimitive("Model", "F1 W02"))
                .addProperty(createPrimitive("ModelYear", "2011"))
                .addProperty(createPrimitive("Price", 167189.00))
                .addProperty(createPrimitive("Currency", "EUR")));

        return entitySet;
    }

    private EntityCollection createManufacturers() {
        EntityCollection entitySet = new EntityCollection();

        entitySet.getEntities().add(new Entity()
                .addProperty(createPrimitive("Id", 1))
                .addProperty(createPrimitive("Name", "Star Powered Racing"))
                .addProperty(createAddress("Star Street 137", "Stuttgart", "70173", "Germany")));

        entitySet.getEntities().add(new Entity()
                .addProperty(createPrimitive("Id", 2))
                .addProperty(createPrimitive("Name", "Horse Powered Racing"))
                .addProperty(createAddress("Horse Street 1", "Maranello", "41053", "Italy")));

        return entitySet;
    }

    private Property createAddress(final String street, final String city, final String zipCode, final String country) {
        List<Property> addressProperties = new ArrayList<Property>();

        addressProperties.add(createPrimitive("Street", street));
        addressProperties.add(createPrimitive("City", city));
        addressProperties.add(createPrimitive("ZipCode", zipCode));
        addressProperties.add(createPrimitive("Country", country));

        return new Property(null, "Address", ValueType.COMPLEX, addressProperties);
    }

    private Property createPrimitive(final String name, final Object value) {
        return new Property(null, name, ValueType.PRIMITIVE, value);
    }

    public EntityCollection readAll(EdmEntitySet edmEntitySet) {
        return data.get(edmEntitySet.getName());
    }

    public Entity read(final EdmEntitySet edmEntitySet, final List<UriParameter> keys) throws Exception {
        final EdmEntityType entityType = edmEntitySet.getEntityType();
        final EntityCollection entitySet = readAll(edmEntitySet);

        if (entitySet == null) {
            return null;
        } else {
            try {
                for (final Entity entity : entitySet.getEntities()) {
                    boolean found = true;
                    for (final UriParameter key : keys) {
                        final EdmProperty property = (EdmProperty) entityType.getProperty(key.getName());
                        final EdmPrimitiveType type = (EdmPrimitiveType) property.getType();

                        if (!type.valueToString(entity.getProperty(key.getName()).getValue(), property.isNullable(),
                                property.getMaxLength(), property.getPrecision(), property.getScale(),
                                property.isUnicode()).equals(key.getText())) {
                            found = false;
                            break;
                        }
                    }
                    if (found) {
                        return entity;
                    }
                }
                return null;
            } catch (final EdmPrimitiveTypeException e) {
                throw new Exception("Wrong key!", e);
            }
        }
    }



}