package io.quarkus.rest.data.panache.deployment.methods.hal;

import static io.quarkus.gizmo.MethodDescriptor.ofMethod;

import javax.ws.rs.core.Response;

import io.quarkus.gizmo.BranchResult;
import io.quarkus.gizmo.BytecodeCreator;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.FieldDescriptor;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.gizmo.ResultHandle;
import io.quarkus.rest.data.panache.RestDataResource;
import io.quarkus.rest.data.panache.deployment.ResourceMetadata;
import io.quarkus.rest.data.panache.deployment.properties.ResourceProperties;
import io.quarkus.rest.data.panache.deployment.utils.ResponseImplementor;

public final class UpdateHalMethodImplementor extends HalMethodImplementor {

    private static final String METHOD_NAME = "updateHal";

    private static final String RESOURCE_UPDATE_METHOD_NAME = "update";

    private static final String RESOURCE_GET_METHOD_NAME = "get";

    /**
     * Expose {@link RestDataResource#update(Object, Object)} via HAL JAX-RS method.
     * Generated code looks more or less like this:
     * 
     * <pre>
     * {@code
     *     &#64;Transactional
     *     &#64;PUT
     *     &#64;Path("{id}")
     *     &#64;Consumes({"application/json"})
     *     &#64;Produces({"application/hal+json"})
     *     public Response updateHal(@PathParam("id") ID id, Entity entityToSave) {
     *         if (resource.get(id) != null) {
     *             resource.update(id, entityToSave);
     *             return Response.status(204).build();
     *         } else {
     *             Entity entity = resource.update(id, entityToSave);
     *             HalEntityWrapper wrapper = new HalEntityWrapper(entity);
     *             String location = new ResourceLinksProvider().getSelfLink(entity);
     *             if (location != null) {
     *                 ResponseBuilder responseBuilder = Response.status(201);
     *                 responseBuilder.entity(wrapper);
     *                 responseBuilder.location(URI.create(location));
     *                 return responseBuilder.build();
     *             } else {
     *                 throw new RuntimeException("Could not extract a new entity URL")
     *             }
     *         }
     *     }
     * }
     * </pre>
     */
    @Override
    protected void implementInternal(ClassCreator classCreator, ResourceMetadata resourceMetadata,
            ResourceProperties resourceProperties, FieldDescriptor resourceField) {
        MethodCreator methodCreator = classCreator.getMethodCreator(METHOD_NAME, Response.class.getName(),
                resourceMetadata.getIdType(), resourceMetadata.getEntityType());

        // Add method annotations
        addPathAnnotation(methodCreator,
                appendToPath(resourceProperties.getMethodPath(RESOURCE_UPDATE_METHOD_NAME), "{id}"));
        addTransactionalAnnotation(methodCreator);
        addPutAnnotation(methodCreator);
        addPathParamAnnotation(methodCreator.getParameterAnnotations(0), "id");
        addConsumesAnnotation(methodCreator, APPLICATION_JSON);
        addProducesAnnotation(methodCreator, APPLICATION_HAL_JSON);

        // Invoke resource methods
        ResultHandle resource = methodCreator.readInstanceField(resourceField, methodCreator.getThis());
        ResultHandle id = methodCreator.getMethodParam(0);
        ResultHandle entityToSave = methodCreator.getMethodParam(1);

        // Wrap and return response
        BranchResult entityExists = doesEntityExist(methodCreator, resourceMetadata.getResourceClass(), resource, id);
        updateAndReturn(entityExists.trueBranch(), resourceMetadata.getResourceClass(), resource, id, entityToSave);
        createAndReturn(entityExists.falseBranch(), resourceMetadata.getResourceClass(), resource, id, entityToSave);
        methodCreator.close();
    }

    @Override
    protected String getResourceMethodName() {
        return RESOURCE_UPDATE_METHOD_NAME;
    }

    private BranchResult doesEntityExist(BytecodeCreator creator, String resourceClass, ResultHandle resource,
            ResultHandle id) {
        ResultHandle entity = creator.invokeVirtualMethod(
                ofMethod(resourceClass, RESOURCE_GET_METHOD_NAME, Object.class, Object.class), resource, id);
        return creator.ifNotNull(entity);
    }

    private void createAndReturn(BytecodeCreator creator, String resourceClass, ResultHandle resource, ResultHandle id,
            ResultHandle entityToSave) {
        ResultHandle entity = creator.invokeVirtualMethod(
                ofMethod(resourceClass, RESOURCE_UPDATE_METHOD_NAME, Object.class, Object.class, Object.class),
                resource, id, entityToSave);
        ResultHandle wrapper = wrapHalEntity(creator, entity);
        ResultHandle entityUrl = ResponseImplementor.getEntityUrl(creator, entity);
        creator.returnValue(ResponseImplementor.created(creator, wrapper, entityUrl));
    }

    private void updateAndReturn(BytecodeCreator creator, String resourceClass, ResultHandle resource, ResultHandle id,
            ResultHandle entityToSave) {
        creator.invokeVirtualMethod(
                ofMethod(resourceClass, RESOURCE_UPDATE_METHOD_NAME, Object.class, Object.class, Object.class),
                resource, id, entityToSave);
        creator.returnValue(ResponseImplementor.noContent(creator));
    }
}
