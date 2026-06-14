package com.popcorn.soundcloudclone.features.user.service.impl;

import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
import com.popcorn.soundcloudclone.features.user.service.UserService;
import com.popcorn.soundcloudclone.features.users.dto.request.UserUpdateRequest;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.mapper.UserMapper;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    @Cacheable(value = "users", key = "#id")
    public UserResponse getUserProfileById(int id) {
        return userMapper.toUserResponse(findUserByIdOrThrow(id));
    }

    @Cacheable(value = "usersByUsername", key = "#username")
    public UserResponse getUserProfileByUsername(String username) {
        var found = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("User not found with username: " + username,
                        ErrorCode.NOT_FOUND));
        return userMapper.toUserResponse(found);
    }

    private User findUserByIdOrThrow(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("User not found with id: " + id, ErrorCode.NOT_FOUND));
    }

    @Transactional
    @CachePut(value = "users", key = "#userId")
    @CacheEvict(value = "usersByUsername", key = "#result.username")
    public UserResponse patchUpdateUser(int userId, UserUpdateRequest request) {
        User user = findUserByIdOrThrow(userId);
        // dung mapper de map, ignore avatar, password, background
        userMapper.updateUser(user, request);
        userRepository.flush(); // flush de check unique, constrain

        return userMapper.toUserResponse(userRepository.save(user));

    }
}
