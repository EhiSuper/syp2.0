import { Playlist } from "./playlist";
import { userComment } from "./userComment";

export interface Song {
    id: string
    track: string
    artist: string
    playlists: Playlist[]
    comments?: userComment[]
}