package com.example.chatserver.services;
import com.example.chatserver.controllers.dtos.BaseResponse;
import com.example.chatserver.controllers.dtos.BaseUserRequest;
import com.example.chatserver.controllers.dtos.GetUserResponse;
import com.example.chatserver.documents.User;
import com.example.chatserver.repositories.IUserRepository;
import com.example.chatserver.services.intefaces.IUserServices;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements IUserServices {

    @Autowired
    private IUserRepository repository;

    @Override
    public BaseResponse create(BaseUserRequest request) {

        Optional<User> possible = repository.findByName(request.getName());

        if (possible.isPresent()){
            return BaseResponse.builder()
                    .data(null)
                    .success(false)
                    .message("El usuario ya existe")
                    .status(HttpStatus.CREATED)
                    .build();
        }

        return BaseResponse.builder()
                .data(repository.save(from(request)))
                .success(true)
                .message("Usuario creado")
                .status(HttpStatus.CREATED)
                .build();
    }

    @Override
    public BaseResponse login(BaseUserRequest request) {

        Optional<User> possible = repository.findByName(request.getName());

        BaseResponse baseResponse = BaseResponse.builder()
                .success(false)
                .message("El usuario o la contraseña no son correctos")
                .data(null)
                .status(HttpStatus.BAD_REQUEST).build();

        if(possible.isPresent()){
            User user = possible.get();
            if(BCrypt.checkpw(request.getPassword(), user.getPassword())){

                user.setIsLogged(true);
                repository.save(user);

                return BaseResponse.builder()
                        .success(true)
                        .message(" inicio de sesion aprovado ")
                        .data(from(possible.get()))
                        .status(HttpStatus.ACCEPTED).build();
            }

            return baseResponse;

        }

        return baseResponse;
    }

    @Override
    public void exit(String userName) {
        Optional<User> possible = repository.findByName(userName);
        if(possible.isPresent()){
            User user = possible.get();
            user.setIsLogged(false);
            repository.save(user);
        }
    }

    private User from(BaseUserRequest request){
        User user = new User();
        user.setName(request.getName());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(10)));
        return user;
    }

    private GetUserResponse from(User user){
        return GetUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .password(user.getPassword()).build();
    }

}
