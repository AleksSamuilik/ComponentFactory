package com.alex.factory.service;

import com.alex.factory.dto.Factory;
import com.alex.factory.exception.CompFactSuchUserAlreadyExistException;
import com.alex.factory.model.*;
import com.alex.factory.repository.*;
import com.alex.factory.utils.ParamsBusinessLogic;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FactoryService {
    private final AuthInfoRepository authInfoRepository;
    private final ParamsBusinessLogic paramsBusinessLogic;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RelationTypeRepository relationTypeRepository;
    private final UserDescriptionRepository userDescriptionRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;
    private final CompanyService companyService;

    public void signUp(Factory signUpRequest) throws CompFactSuchUserAlreadyExistException {
        if (authInfoRepository.findByLogin(signUpRequest.getEmail()).isPresent()) {
            throw new CompFactSuchUserAlreadyExistException("Account with email: " + signUpRequest.getEmail() + " already exists");
        }
        save(signUpRequest);
    }

    private void save(Factory signUpRequest) {
        final RelationType relationType = new RelationType();
        final FactoryDescription factoryDescription = new FactoryDescription();
        factoryDescription.setPhone(signUpRequest.getPhone());
        factoryDescription.setPosition(signUpRequest.getPosition());
        relationType.setFactoryDescription(factoryDescription);
        final RelationType savedRelationType = relationTypeRepository.save(relationType);
        final Optional<Role> role = roleRepository.findByName(UserRole.ADMIN);
        saveUserDescription(signUpRequest, savedRelationType, role);
    }

    private void saveUserDescription(final Factory signUpRequest, final RelationType savedRelationType, Optional<Role> role) {
        final UserDescription userDescription = new UserDescription();
        userDescription.setRole(role.get());
        userDescription.setRelationType(savedRelationType);
        final UserDescription savedUserDescription = userDescriptionRepository.save(userDescription);
        saveUser(signUpRequest, savedUserDescription);
    }

    private void saveUser(final Factory signUpRequest, final UserDescription savedUserDescription) {
        final User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setFullName(signUpRequest.getFullName());
        user.setUsersDescription(savedUserDescription);
        final User savedUser = userRepository.save(user);
        saveAuthInfo(signUpRequest, savedUser);
    }


    private void saveAuthInfo(final Factory request, final User savedUser) {
        final AuthInfoEntity authInfoEntity = new AuthInfoEntity();
        authInfoEntity.setLogin(request.getEmail());
        authInfoEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        authInfoEntity.setUser(savedUser);
        authInfoRepository.save(authInfoEntity);
    }

}
