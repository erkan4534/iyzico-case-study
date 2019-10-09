package com.iyzico.challenge.integrator.controller.management;

import com.iyzico.challenge.integrator.data.entity.User;
import com.iyzico.challenge.integrator.data.service.UserService;
import com.iyzico.challenge.integrator.dto.ListResponse;
import com.iyzico.challenge.integrator.dto.UserDto;
import com.iyzico.challenge.integrator.dto.user.CreateUserRequest;
import com.iyzico.challenge.integrator.dto.user.UpdateUserRequest;
import com.iyzico.challenge.integrator.mapper.UserMapper;
import com.iyzico.challenge.integrator.session.AdminEndpoint;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedList;

@RestController
@AdminEndpoint
@RequestMapping("management/user")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService,
                          UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @ApiOperation(
            value = "Get User With Id",
            notes = "Gets the user with given id that belongs to the same agency"
    )
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public UserDto get(@PathVariable("id") int id) {
        User user = userService.getById(id);
        return userMapper.map(user);
    }

    @ApiOperation(
            value = "Get Users of the Agency",
            notes = "Gets the users of the agency that the current admin user belongs to"
    )
    @RequestMapping(method = RequestMethod.GET)
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public ListResponse<UserDto> get() {
        ListResponse<UserDto> response = new ListResponse<>();
        LinkedList<UserDto> users = new LinkedList<>();
        for (User user : userService.getAllUsers()) {
            users.add(userMapper.map(user));
        }
        response.setItems(users);
        return response;
    }

    @ApiOperation(
            value = "Create User",
            notes = "Creates a new user"
    )
    @RequestMapping(method = RequestMethod.PUT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserDto create(@RequestBody @Valid CreateUserRequest request) {
        User user = userService.createUser(request.getName(), request.getUsername(), request.getPassword(), request.isAdmin());
        return userMapper.map(user);
    }

    @ApiOperation(
            value = "Update User",
            notes = "Updates the user which specified by id in request body with the expected parameters"
    )
    @RequestMapping(method = RequestMethod.POST)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserDto update(@ModelAttribute("request") @Valid UpdateUserRequest request) {
        User user = userService.updateUser(request.getId(), request.getName(), request.getPassword(), request.isAdmin());
        return userMapper.map(user);
    }

    @ApiOperation(
            value = "Delete User With Id",
            notes = "Deletes the user with given id that belongs to the same agency"
    )
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(@PathVariable("id") int id) {
        userService.delete(id);
    }
}