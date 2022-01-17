import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { User } from '../interfaces/user';
import { Playlist } from '../interfaces/playlist';
import { userComment } from '../interfaces/userComment';

@Injectable({ providedIn: 'root' })
export class UserService {

  private serverUrl = "http://localhost:8080"
  private usersUrl = this.serverUrl + '/api/users';  // URL to web api

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) { }

  //provo a utilizzare searchUsers

  /** GET user by username. Will 404 if id not found */
  getUserByUsername(username: string): Observable<User[]> {
    const url = `${this.usersUrl}/login?username=${username}`;
    return this.http.get<User[]>(url).pipe(
      catchError(this.handleError<User[]>(`getUsers username=${username}`))
    );
  }

  /** GET user by id. Will 404 if id not found */
  getUser(id: string): Observable<User> {
    const url = `${this.usersUrl}/${id}`;
    return this.http.get<User>(url).pipe(
      catchError(this.handleError<User>(`getUser id=${id}`))
    );
  }

  getTopUsers(): Observable<User[]> {
    const url = `${this.usersUrl}/mostfollowed`;
    return this.http.get<User[]>(url).pipe(
      catchError(this.handleError<User[]>(`getTopUsers`))
    );
  }

  /* GET users whose name contains search term */
  searchUsers(term: string): Observable<User[]> {
    if (!term.trim()) {
      // if not search term, return empty hero array.
      return of([]);
    }
    return this.http.get<User[]>(`${this.usersUrl}?username=${term}`).pipe(
      catchError(this.handleError<User[]>('searchUser', []))
    );
  }

  /** PUT: update the user on the server */
  updateUser(userOld: User, userNew: User): Observable<any> {
    var list = [userOld, userNew]
    return this.http.put(this.usersUrl, list, this.httpOptions)
      //.pipe(catchError(this.handleError<any>('updateUser')));
  }

  /** DELETE: delete the user from the server */
  deleteUser(id: string): void {
    const url = `${this.usersUrl}/${id}`;
    this.http.delete<User>(url, this.httpOptions).pipe(
      catchError(this.handleError<User>('deleteUser'))
    ).subscribe();
  }

  /** POST: add a new user to the server */
  addUser(user: User): Observable<User> {
    return this.http.post<User>(this.usersUrl, user, this.httpOptions).pipe(
      catchError(this.handleError<User>('addUser'))
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

  // a user follow a user on the server
  follow(follower: string, followed: string): Observable<any> {
    const url = `${this.serverUrl}/api/follow?follower=${follower}&followed=${followed}`;
    return this.http.get<Playlist>(url);
  }

  // a user unfollow a user on the server
  unfollow(follower: string, followed: string): Observable<any> {
    const url = `${this.serverUrl}/api/unfollow?follower=${follower}&followed=${followed}`;
    return this.http.get<Playlist>(url);
  }

  /** GET user by id. Will 404 if id not found */
  getPlaylistsFollowed(id: string): Observable<Playlist[]> {
    const url = `${this.usersUrl}/playlistsfollowed/${id}`;
    return this.http.get<Playlist[]>(url).pipe(
      catchError(this.handleError<Playlist[]>(`getPlaylistsFollowed id=${id}`))
    );
  }

  /** GET user by id. Will 404 if id not found */
  getFollowed(id: string): Observable<User[]> {
    const url = `${this.usersUrl}/followed/${id}`;
    return this.http.get<User[]>(url).pipe(
      catchError(this.handleError<User[]>(`getFollowed id=${id}`))
    );
  }

  /** GET user by id. Will 404 if id not found */
  getFollowers(id: string): Observable<User[]> {
    const url = `${this.usersUrl}/followers/${id}`;
    return this.http.get<User[]>(url).pipe(
      catchError(this.handleError<User[]>(`getFollowers id=${id}`))
    );
  }
}
