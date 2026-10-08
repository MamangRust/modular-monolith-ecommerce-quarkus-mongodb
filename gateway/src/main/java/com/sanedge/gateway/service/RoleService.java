package com.sanedge.gateway.service;

import com.sanedge.gateway.dto.RoleDto;
import io.smallrye.mutiny.Uni;

public interface RoleService {
    Uni<RoleDto.ApiResponsePaginationRole> listRoles(int page, int size, String search);
    Uni<RoleDto.ApiResponseRole> getRole(String id);
    Uni<RoleDto.ApiResponseRole> getRoleByName(String name);
    Uni<RoleDto.ApiResponsePaginationRoleDeleteAt> getActiveRoles(int page, int size, String search);
    Uni<RoleDto.ApiResponsePaginationRoleDeleteAt> getTrashedRoles(int page, int size, String search);
    Uni<RoleDto.ApiResponsesRole> getRolesByUserId(String userId);

    Uni<RoleDto.ApiResponseRole> createRole(pb.role.RoleCommand.CreateRoleRequest req);
    Uni<RoleDto.ApiResponseRole> updateRole(String id, pb.role.RoleCommand.UpdateRoleRequest req);
    Uni<RoleDto.ApiResponseRoleDeleteAt> deleteRole(String id);
    Uni<RoleDto.ApiResponseRoleDeleteAt> restoreRole(String id);
    Uni<RoleDto.SimpleResponse> deleteRolePermanent(String id);
    Uni<RoleDto.SimpleResponse> restoreAllRole();
    Uni<RoleDto.SimpleResponse> deleteAllRolePermanent();

    Uni<RoleDto.ApiResponseUserRole> assignRoleToUser(pb.role.RoleCommon.AssignRoleToUserRequest req);
    Uni<Void> removeRoleFromUser(pb.role.RoleCommon.RemoveRoleFromUserRequest req);
}
