package com.sanedge.gateway.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sanedge.gateway.service.OrderItemService;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/order-items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Order Items", description = "Order item query endpoints")
public class OrderItemResource {

    @Inject
    OrderItemService orderItemService;

    @GET
    @RolesAllowed({ "ROLE_ADMIN", "ROLE_STAFF", "ROLE_USER" })
    @Operation(summary = "List all order items")
    public Uni<Response> findAll(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("search") String search) {
        return orderItemService.findAll(page, size, search)
                .map(dto -> Response.ok(dto).build());
    }

    @GET
    @Path("/active")
    @RolesAllowed({ "ROLE_ADMIN", "ROLE_STAFF", "ROLE_USER" })
    @Operation(summary = "List active order items")
    public Uni<Response> findByActive(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("search") String search) {
        return orderItemService.findByActive(page, size, search)
                .map(dto -> Response.ok(dto).build());
    }

    @GET
    @Path("/trashed")
    @RolesAllowed({ "ROLE_ADMIN", "ROLE_STAFF" })
    @Operation(summary = "List trashed order items")
    public Uni<Response> findByTrashed(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("search") String search) {
        return orderItemService.findByTrashed(page, size, search)
                .map(dto -> Response.ok(dto).build());
    }

    @GET
    @Path("/order/{orderId}")
    @RolesAllowed({ "ROLE_ADMIN", "ROLE_STAFF", "ROLE_USER" })
    @Operation(summary = "Get order items by order")
    public Uni<Response> findOrderItemByOrder(@PathParam("orderId") String orderId) {
        return orderItemService.findOrderItemByOrder(orderId)
                .map(dto -> Response.ok(dto).build());
    }
}
