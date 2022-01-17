import { Playlist } from "./playlist";
import { userComment } from "./userComment";

export interface User {
    id: string
    username: string
    password: string
    isAdmin: boolean
    dateOfCreation: Date
    dateOfBirth: Date
    playlistsCreated?: Playlist[]
    playlistsFollowed?: Playlist[]
    comments?: userComment[]
    followers?: User[]
    followed?: User[]
}
