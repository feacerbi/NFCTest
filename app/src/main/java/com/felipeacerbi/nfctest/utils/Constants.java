package com.felipeacerbi.nfctest.utils;


public class Constants {

    public static final String PACKAGE = "com.felipeacerbi.nfctest.utils";
    public static final int TABS_NUMBER = 3;
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final int START_WAIT_READ_TAG_INTENT = 1;
    public static final int START_WAIT_WRITE_TAG_INTENT = 2;
    public static final int START_RC_SIGN_IN_INTENT = 3;
    public static final int GAME_REQUEST_NOTIFICATION = 4;
    public static final int RC_BARCODE_CAPTURE = 5;
    public static final int GET_IMAGE_FROM_PICKER = 6;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 7;

    public static final String ACTION_PLAYER_REFUSE_REQUEST = PACKAGE + ".PLAYER_REFUSE_REQUEST";
    public static final String POSITION = "position";
    public static final String LOGIN_ANONYMOUS = "anonymous";
    public static final String PLAYER_ONE = "one";
    public static final String PLAYER_TWO = "two";
    public static final String NFC_TAG_EXTRA = "nfc_tag";

    public static final int POST_TYPE_TEXT = 0;
    public static final int POST_TYPE_PHOTO = 1;

    public static final String DATABASE_GAMES_PATH = "games/";
    public static final String DATABASE_USERS_PATH = "users/";
    public static final String DATABASE_REQUESTS_PATH = "requests/";
    public static final String DATABASE_TAGS_PATH = "tags/";

    public static final String DATABASE_USERS_CHILD = "users";
    public static final String DATABASE_TAGS_CHILD = "tags";
    public static final String DATABASE_NAME_CHILD = "name";
    public static final String DATABASE_EMAIL_CHILD = "email";
    public static final String DATABASE_ONLINE_CHILD = "onlime";
    public static final String DATABASE_PLAYING_CHILD = "playing";
    public static final String DATABASE_IDTOKEN_CHILD = "idToken";
    public static final String DATABASE_PLACES_CHILD = "places";
    public static final String DATABASE_CURRENT_TURN_CHILD = "currentTurn";

    public static final String STORAGE_IMAGES_PATH = "images/";

}
