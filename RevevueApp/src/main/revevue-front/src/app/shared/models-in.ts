export interface UserRegister {
  username : string,
  email : string,
  login : string,
  password : string
}

export interface UserCredentials {
    login : string,
    password : string
}

export interface UserFollowInfo {
    username : string,
    isFollowing : boolean,
    id : string
}
