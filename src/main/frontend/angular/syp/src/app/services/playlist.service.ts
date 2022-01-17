import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Playlist } from '../interfaces/playlist';
import { User } from '../interfaces/user';
import { Song } from '../interfaces/song';

@Injectable({ providedIn: 'root' })
export class PlaylistService {

  private serverUrl = "http://localhost:8080"
  private playlistsUrl = this.serverUrl + '/api/playlists';  // URL to web api

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) { }

  //Get the playlists to be displayed in the dashboard
  getTopPlaylists(): Observable<Playlist[]> {
    const url = `${this.playlistsUrl}/dashboard`;
    return this.http.get<Playlist[]>(url).pipe(
      catchError(this.handleError<Playlist[]>(`getTopPlaylists`))
    );
  }

  getSuggestedPlaylists(username: string): Observable<Playlist[]> {
    const url = `${this.playlistsUrl}/dashboard?username=${username}`;
    return this.http.get<Playlist[]>(url).pipe(
      catchError(this.handleError<Playlist[]>(`getTopPlaylists`))
    );
  }

  /** GET playlist by id. Will 404 if id not found */
  getPlaylist(id: string): Observable<Playlist> {
    const url = `${this.playlistsUrl}/${id}`;
    return this.http.get<Playlist>(url).pipe(
      catchError(this.handleError<Playlist>(`getPlaylist id=${id}`))
    );
  }

  /* GET playlists whose name contains search term */
  searchPlaylists(term: string): Observable<Playlist[]> {
    if (!term.trim()) {
      // if not search term, return empty hero array.
      return of([]);
    }
    return this.http.get<Playlist[]>(`${this.playlistsUrl}/?name=${term}`).pipe(
      catchError(this.handleError<Playlist[]>('searchPlaylists', []))
    );
  }

  /** PUT: update the playlist on the server */
  updatePlaylist(playlistOld: Playlist, playlistNew: Playlist): Observable<any> {
    var list = [playlistOld, playlistNew]
    return this.http.put(this.playlistsUrl, list, this.httpOptions);
  }

  /** DELETE: delete the playlist from the server */
  deletePlaylist(id: string): void {
    const url = `${this.playlistsUrl}/${id}`;

    this.http.delete<Playlist>(url, this.httpOptions).pipe(
      catchError(this.handleError<Playlist>('deletePlaylist'))
    ).subscribe();
  }

  /** POST: add a new playlist to the server */
  addPlaylist(playlist: Playlist): Observable<Playlist> {
    return this.http.post<Playlist>(this.playlistsUrl, playlist, this.httpOptions).pipe(
      catchError(this.handleError<Playlist>('addPlaylist'))
    );
  }

  // a user follow a playlist on the server
  likePlaylist(userId: string, playlistId: string): Observable<any> {
    const url = `${this.serverUrl}/api/like?userId=${userId}&playlistId=${playlistId}`;
    return this.http.get<Playlist>(url)
  }

  // a user unfollow a playlist on the server
  dislikePlaylist(userId: string, playlistId: string): Observable<any> {
    const url = `${this.serverUrl}/api/dislike?userId=${userId}&playlistId=${playlistId}`;
    return this.http.get<Playlist>(url)
  }

  /** GET playlist's follower. Will 404 if id not found */
  getFollowers(id: string): Observable<User[]> {
    const url = `${this.playlistsUrl}/followers/${id}`;
    return this.http.get<User[]>(url).pipe(
      catchError(this.handleError<User[]>(`getFollowers id=${id}`))
    );
  }

  /**
 * Handle Http operation that failed.
 * Let the app continue.
 * @param operation - name of the operation that failed
 * @param result - optional value to return as the observable result
 */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      window.alert("operation failed")
      console.error(error); // log to console instead

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  getResultNumber(endpoint: string): Observable<number> {
    const url = `${this.serverUrl}${endpoint}`;
    return this.http.get<number>(url).pipe(
      catchError(this.handleError<number>(`getResultNumber`))
    );
  }

  getResultUsers(endpoint: string): Observable<User[]> {
    const url = `${this.serverUrl}${endpoint}`;
    return this.http.get<User[]>(url).pipe(
      catchError(this.handleError<User[]>(`getResultUsers`))
    );
  }

  getResultPlaylists(endpoint: string): Observable<Playlist[]> {
    const url = `${this.serverUrl}${endpoint}`;
    return this.http.get<Playlist[]>(url).pipe(
      catchError(this.handleError<Playlist[]>(`getResultPlaylist`))
    );
  }

  getResultSongs(endpoint: string): Observable<Song[]> {
    const url = `${this.serverUrl}${endpoint}`;
    return this.http.get<Song[]>(url).pipe(
      catchError(this.handleError<Song[]>(`getResultSongs`))
    );
  }
}
