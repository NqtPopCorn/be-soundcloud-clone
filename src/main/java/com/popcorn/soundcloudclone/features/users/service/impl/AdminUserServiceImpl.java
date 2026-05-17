package com.popcorn.soundcloudclone.features.users.service.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.users.dto.request.AdminCreationUserRequest;
import com.popcorn.soundcloudclone.features.users.dto.request.AdminUpdateUserRequest;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.mapper.UserMapper;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import com.popcorn.soundcloudclone.features.users.repository.specification.UserSpecification;
import com.popcorn.soundcloudclone.features.users.service.AdminUserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(AdminCreationUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_USER);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .active(true)
                .build();

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public PageResponse<UserResponse> getPageUsers(String keyword, Pageable pageable) {
        Specification<User> spec = UserSpecification.keywordContains(keyword);
        var page = userRepository.findAll(spec, pageable);

        return PageResponse.from((page.map(userMapper::toUserResponse)));
    }

    @Override
    public UserResponse updateUser(int userId, AdminUpdateUserRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        user.setRole(User.Role.valueOf(request.getRole()));
        user.setActive(request.getActive());
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponse getUserById(int userId) {
        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND)));
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        return userMapper.toUserResponse(userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND)));
    }
}
