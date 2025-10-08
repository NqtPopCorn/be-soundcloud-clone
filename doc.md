# API DOC

## User update
update avatar hien co neu khac file upload khac null, nguoc lai khong update file nao ca 
PUT host/users/me
{
    ...
    "avatarUpload": nullable
    "backgroundUpload": nullable
}

## User delete, update image(avatar, background)
PUT/DELETE host/users/me/{avatar/background}

# TrackLike, TrackPlay, AlbumLike, PlaylistLike

