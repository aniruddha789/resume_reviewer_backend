package com.resumereviewer.webapp.service;

import com.resumereviewer.webapp.dto.request.LoginDTO;
import com.resumereviewer.webapp.dto.request.RegisterDTO;
import com.resumereviewer.webapp.dto.response.Status;
import com.resumereviewer.webapp.entity.Role;
import com.resumereviewer.webapp.entity.UserEntity;
import com.resumereviewer.webapp.repository.UserEntityRepository;
import com.resumereviewer.webapp.util.StatusBuilder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserEntityRepository userEntityRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptEncoder;

    public UserEntity findById(Integer id) {
        return userEntityRepository.findById(id).get();
    }

    public UserEntity findByUsername(String username) {
        return userEntityRepository.findByUsername(username);
    }

    public UserEntity findByEmail(String email) {
        return userEntityRepository.findByEmail(email);
    }

    public ArrayList<Role> getRoles(String username) {
        UserEntity user = this.userEntityRepository.findByUsername(username);
        return (ArrayList<Role>) user.getRoles();
    }

    public ArrayList<String> getAllUsers(){
        return userEntityRepository.findAll().stream().map(a -> a.getUsername()).collect(Collectors.toCollection(ArrayList::new));
    }

    @Transactional
    public Status deleteUser(LoginDTO loginDTO) {
        UserEntity findUser = userEntityRepository.findByUsername(loginDTO.getUsername());

        if(findUser == null){
            return new StatusBuilder()
                    .status("FAIL")
                    .code("402")
                    .message("User does not exist")
                    .build();

        } else if (!bCryptEncoder.matches(loginDTO.getPassword(), findUser.getPassword())) {
            return new StatusBuilder()
                    .status("FAIL")
                    .code("403")
                    .message("Wrong password!")
                    .build();
        }

        userEntityRepository.deleteByUsername(loginDTO.getUsername());

        return new StatusBuilder()
                .status("SUCCESSFUL")
                .code("200")
                .message("User deleted successfully!")
                .build();
    }

    public Status updatePassword(LoginDTO loginDTO) {

        UserEntity findUser = userEntityRepository.findByUsername(loginDTO.getUsername());

        if(findUser == null){
            return new StatusBuilder()
                    .status("FAIL")
                    .code("402")
                    .message("User does not exist")
                    .build();
        }

        String encryptedPass = bCryptEncoder.encode(loginDTO.getPassword());

        findUser.setPassword(encryptedPass);

        userEntityRepository.save(findUser);

        return new StatusBuilder()
                .status("SUCCESSFUL")
                .code("200")
                .message("Password updated successfully!")
                .build();

    }

    public Status register(RegisterDTO registerDTO){

        UserEntity findUser = findByUsername(registerDTO.getUsername());
        if(findUser != null){
            return new StatusBuilder()
                    .status("FAIL")
                    .message("Username already exists!")
                    .code("400")
                    .build();
        }

        findUser = findByEmail(registerDTO.getEmail());
        if(findUser != null){
            return new StatusBuilder()
                    .status("FAIL")
                    .message("Email already exists!")
                    .code("401")
                    .build();
        }

        UserEntity userEntity = new UserEntity(registerDTO.getFirstname(),
                registerDTO.getLastname(),
                registerDTO.getUsername(),
                bCryptEncoder.encode(registerDTO.getPassword()),
                registerDTO.getEmail(),
                registerDTO.getPhone());

        /** Set firebase UID to link with firebase user */
        userEntity.setFirebaseUid(registerDTO.getFirebaseUid());

        //set the roles
        userEntity.setRoles(registerDTO.getRoles());

        userEntityRepository.save(userEntity);

        return new StatusBuilder()
                .status("SUCCESS")
                .message("User created successfully!")
                .code("200")
                .build();
    }


    public Status loginUser(LoginDTO loginDTO) {
        UserEntity findUser = userEntityRepository.findByUsername(loginDTO.getUsername());

        if(findUser == null){
            return new StatusBuilder()
                    .status("FAIL")
                    .code("402")
                    .message("User does not exist")
                    .build();

        } else if (!bCryptEncoder.matches(loginDTO.getPassword(), findUser.getPassword())) {
            return new StatusBuilder()
                    .status("FAIL")
                    .code("403")
                    .message("Wrong password!")
                    .build();
        }

        return new StatusBuilder()
                .status("SUCCESS")
                .code("200")
                .message("User logged in successfully!")
                .build();

    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = findByUsername(username);
        User springUser = null;

        if(userEntity == null){
            throw new UsernameNotFoundException("User: " + username + " not found!");
        } else {

            List<Role> roles = userEntity.getRoles();
            Set<GrantedAuthority> ga = new HashSet<>();
            for (Role role : roles) {
                ga.add(new SimpleGrantedAuthority(role.getName()));
            }

            springUser = new User(userEntity.getUsername(), userEntity.getPassword(), ga);

        }

        return springUser;

    }



}
