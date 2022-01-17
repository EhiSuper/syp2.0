import { Playlist } from "./playlist";
import { userComment } from "./userComment";

export interface Song {
    id: string
    track: string
    artist: string
    year: string
    lyric: string
    album: string
    playlists: Playlist[]
    comments?: userComment[]
}