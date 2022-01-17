import { Song } from "./song";
import { User } from "./user";

export interface Playlist {
    id: string
    name: string
    creationDate: Date
    creator: User
    songs: Song[]
    followers?: User[]
  }