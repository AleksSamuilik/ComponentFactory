package com.alex.factory.service;

import com.alex.factory.dto.Company;
import com.alex.factory.exception.CompFactSuchUserAlreadyExistException;
import com.alex.factory.model.*;
import com.alex.factory.repository.*;
import com.alex.factory.security.UserRole;
import com.alex.factory.utils.ParamsBusinessLogic;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.Optional;

@Log
@Service
@AllArgsConstructor
public class CompanyService {


    private final AuthInfoRepository authInfoRepository;
    private final ParamsBusinessLogic paramsBusinessLogic;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RelationTypeRepository relationTypeRepository;
    private final UserDescriptionRepository userDescriptionRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void signUp(final Company signUpRequest) throws CompFactSuchUserAlreadyExistException {

        if (authInfoRepository.findByLogin(signUpRequest.getEmail()).isPresent()) {
            throw new CompFactSuchUserAlreadyExistException("Account with email: " + signUpRequest.getEmail() + " already exists");
        }
        save(signUpRequest);
    }

    private void save(final Company signUpRequest) {
        final RelationType relationType = new RelationType();
        CompanyDescription companyDescription = new CompanyDescription();
        companyDescription.setCompany(signUpRequest.getCompany());
        companyDescription.setPhone(signUpRequest.getPhone());
        companyDescription.setInfo(signUpRequest.getInfo());
        companyDescription.setDiscount(paramsBusinessLogic.getDISCOUNT_MEDIUM());
        relationType.setCompanyDescription(companyDescription);
        final RelationType savedRelationType = relationTypeRepository.save(relationType);
        final Optional<Role> role = roleRepository.findByName(UserRole.USER);
        saveUserDescription(signUpRequest, savedRelationType, role);
    }

    private void saveUserDescription(final Company signUpRequest, final RelationType savedRelationType, Optional<Role> role) {
        final UserDescription userDescription = new UserDescription();
        userDescription.setRole(role.get());
        userDescription.setRelationType(savedRelationType);
        final UserDescription savedUserDescription = userDescriptionRepository.save(userDescription);
        saveUser(signUpRequest, savedUserDescription);
    }

    private void saveUser(final Company signUpRequest, final UserDescription savedUserDescription) {
        final User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setFullName(signUpRequest.getFullName());
        user.setUsersDescription(savedUserDescription);
        final User savedUser = userRepository.save(user);
        saveAuthInfo(signUpRequest, savedUser);

    }

    private void saveAuthInfo(final Company request, final User savedUser) {
        final AuthInfoEntity authInfoEntity = new AuthInfoEntity();
        authInfoEntity.setLogin(request.getEmail());
        authInfoEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        authInfoEntity.setUser(savedUser);
        authInfoRepository.save(authInfoEntity);
    }

    @Transactional
    public void delete(Long userId) {
        final Optional<AuthInfoEntity> authInfoEntity = authInfoRepository.findById(userId);
        if (authInfoEntity.isPresent()) {
            orderRepository.deleteAllByUser(authInfoEntity.get().getUser());
            authInfoRepository.deleteAllByLogin(authInfoEntity.get().getLogin());
        } else {
            throw new NoSuchElementException("Such Authentication info doesn't exist");
        }
    }
}
