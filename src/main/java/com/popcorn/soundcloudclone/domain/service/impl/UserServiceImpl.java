package com.popcorn.soundcloudclone.domain.service.impl;

import com.popcorn.soundcloudclone.domain.dto.user.*;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.entity.FileUpload;
import com.popcorn.soundcloudclone.domain.entity.User;
import com.popcorn.soundcloudclone.domain.entity.UserFollower;
import com.popcorn.soundcloudclone.exception.ApplicationException;
import com.popcorn.soundcloudclone.exception.ErrorCode;
import com.popcorn.soundcloudclone.mapper.UserMapper;
import com.popcorn.soundcloudclone.domain.repository.UserFollowerRepository;
import com.popcorn.soundcloudclone.domain.repository.UserRepository;
import com.popcorn.soundcloudclone.domain.repository.specification.UserSpecification;
import com.popcorn.soundcloudclone.domain.service.FileUploadService;
import com.popcorn.soundcloudclone.domain.service.UserService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    // PageResponseMapper<UserResponse> pageResponseMapper;
    BCryptPasswordEncoder passwordEncoder;
    FileUploadService fileUploadService;

    @Override
    public UserResponse createRequest(UserCreationRequest request) {
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
                .status(User.Status.ACTIVE)
                .build();

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse createAdminRequest(AdminCreationUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_USER);
        }

        User user = userMapper.toUser(request);
        user.setRole(User.Role.valueOf(request.getRole()));
        user.setStatus(User.Status.valueOf(request.getStatus()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public PageResponse<UserResponse> getPageUsers(String keyword, Pageable pageable) {
        Specification<User> spec = UserSpecification.keywordContains(keyword);
        var page = userRepository.findAll(spec, pageable);

        return PageResponse.from((page.map(userMapper::toUserResponse)));

    }

    public UserResponse getUserProfileById(int id) {
        return userMapper.toUserResponse(findUserByIdOrThrow(id));
    }

    public UserResponse getUserProfileByUsername(String username) {
        var found = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("User not found with username: " + username,
                        ErrorCode.NOT_FOUND));
        // if(found == null) {
        // found = findUserByIdOrThrow(Integer.parseInt(username));
        // }
        return userMapper.toUserResponse(found);
    }

    private User findUserByIdOrThrow(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("User not found with id: " + id, ErrorCode.NOT_FOUND));
    }

    @Transactional
    public UserResponse patchUpdateUser(int userId, UserUpdateRequest request) {
        User user = findUserByIdOrThrow(userId);
        // dung mapper de map, ignore avatar, password, background
        userMapper.updateUser(user, request);
        userRepository.flush(); // flush de check unique, constrain

        var newAvatar = updateImageIfNonNull(request.getAvatarUpload(), user.getAvatarUpload());
        var newBackground = updateImageIfNonNull(request.getBackgroundUpload(), user.getBackgroundUpload());

        user.setAvatarUpload(newAvatar);
        user.setBackgroundUpload(newBackground);

        // co the khong can save vi object da nay da duoc quan ly
        return userMapper.toUserResponse(userRepository.save(user));

    }

    @Override
    @Transactional
    public void updateAvatar(int userId, MultipartFile upload) {
        var user = findUserByIdOrThrow(userId);
        FileUpload existing = user.getAvatarUpload();
        // file null thi xoa, khac null thi update
        // update neu file non null
        var newAvatar = updateImageIfNonNull(upload, existing);
        user.setAvatarUpload(newAvatar);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateBackgroundImage(int userId, MultipartFile upload) {
        var user = findUserByIdOrThrow(userId);
        FileUpload existing = user.getBackgroundUpload();
        // update neu file non null
        var newBackground = updateImageIfNonNull(upload, existing);
        user.setBackgroundUpload(newBackground);
        userRepository.save(user);
    }

    // @Override
    // @Transactional
    // public void followUser(int userId, int artistId) {
    // if( !userFollowerRepository.existsByUserIdAndArtistId(userId, artistId)) {
    // if(userId == artistId) throw new
    // ApplicationException(ErrorCode.DUPLICATED_CONSTRAINT);
    // User user = findUserByIdOrThrow(userId);
    // User artist = findUserByIdOrThrow(artistId);
    // UserFollower follow = UserFollower.builder()
    // .user(user)
    // .artist(artist)
    // .build();
    // userFollowerRepository.save(follow);
    // userFollowerRepository.flush();
    // user.setFollowingCount(user.getFollowingCount() + 1);
    // artist.setFollowersCount(artist.getFollowersCount() + 1);
    // }
    // }
    //
    // @Override
    // @Transactional
    // public void unFollowUser(int userId, int artistId) {
    // UserFollower follow = userFollowerRepository.findByUserIdAndArtistId(userId,
    // artistId)
    // .orElseThrow(() -> new ApplicationException("User is not following artist: "
    // + artistId, ErrorCode.NOT_FOUND));
    // User user = follow.getUser();
    // User artist = follow.getArtist();
    // userFollowerRepository.delete(follow);
    // userFollowerRepository.flush();
    // user.setFollowingCount(user.getFollowingCount() - 1);
    // artist.setFollowersCount(artist.getFollowersCount() - 1);
    // }

    private FileUpload updateImageIfNonNull(MultipartFile upload, FileUpload existing) {
        if (upload != null) {
            if (existing != null) {
                fileUploadService.replaceFile(existing, upload);
                return existing;
            } else {
                return fileUploadService.storeFile(upload, FileUpload.FileType.image);
            }
        }
        return existing;
    }

    @Transactional
    public UserResponse adminUpdateUser(int userId, AdminUpdateUserRequest request) {

        User user = findUserByIdOrThrow(userId);
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            user.setRole(User.Role.valueOf(request.getRole()));
        }
        if (request.getStatus() != null) {
            user.setStatus(User.Status.valueOf(request.getStatus()));
        }

        userRepository.flush();
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(int userId) {

        var user = findUserByIdOrThrow(userId);

        var avatar = user.getAvatarUpload();
        var background = user.getBackgroundUpload();

        if (avatar != null) {
            fileUploadService.deleteFile(avatar.getId());
        }

        if (background != null) {
            fileUploadService.deleteFile(background.getId());
        }

        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public void deleteAvatar(int userId) {
        var user = findUserByIdOrThrow(userId);
        var avatar = user.getAvatarUpload();
        if (avatar != null) {
            fileUploadService.deleteFile(avatar.getId());
        }
        user.setAvatarUpload(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteBackgroundImage(int userId) {
        var user = findUserByIdOrThrow(userId);
        var avatar = user.getBackgroundUpload();
        if (avatar != null) {
            fileUploadService.deleteFile(avatar.getId());
        }
        user.setBackgroundUpload(null);
        userRepository.save(user);
    }
}
