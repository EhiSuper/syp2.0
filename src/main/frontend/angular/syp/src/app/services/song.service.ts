import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Song } from '../interfaces/song';
import { Playlist } from '../interfaces/playlist';
import { userComment } from '../interfaces/userComment';

@Injectable({ providedIn: 'root' })
export class SongService {

  private serverUrl = "http://localhost:8080"
  private songsUrl = this.serverUrl + '/api/songs';  // URL to web api

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) { }


  /** GET songs by id. Will 404 if id not found */
  getSong(id: string): Observable<Song> {
    const url = `${this.songsUrl}/${id}`;
    return this.http.get<Song>(url).pipe(
      catchError(this.handleError<Song>(`getSongs id=${id}`))
    );
  }

  getTopSongs(): Observable<Song[]> {
    const url = `${this.songsUrl}/popular`;
    return this.http.get<Song[]>(url).pipe(
      catchError(this.handleError<Song[]>(`getSongs popular`))
    );
  }

  /* GET songs whose name contains search term */
  searchSongs(term: string): Observable<Song[]> {
    if (!term.trim()) {
      // if not search term, return empty hero array.
      return of([]);
    }
    return this.http.get<Song[]>(`${this.songsUrl}/?track=${term}`).pipe(
      catchError(this.handleError<Song[]>('searchsongs', []))
    );
  }

  /** PUT: update the song on the server */
  updateSong(songOld: Song, songNew: Song): Observable<any> {
    var list = [songOld, songNew]
    return this.http.put(this.songsUrl, list, this.httpOptions);
  }

  /** DELETE: delete the song from the server */
  deleteSong(id: string): void {
    const url = `${this.songsUrl}/${id}`;

    this.http.delete<Song>(url, this.httpOptions).pipe(
      catchError(this.handleError<Song>('deleteSong'))
    ).subscribe();
  }

  /** POST: add a new song to the server */
  addSong(song: Song): void {
    this.http.post<Song>(this.songsUrl, song, this.httpOptions).pipe(
      catchError(this.handleError<Song>('addSong'))
    ).subscribe();
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
}
