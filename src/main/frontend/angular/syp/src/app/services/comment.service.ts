import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

import { User } from '../interfaces/user';
import { userComment } from '../interfaces/userComment';


@Injectable({ providedIn: 'root' })
export class CommentService {

  private serverUrl = "http://localhost:8080"
  private commentsUrl = this.serverUrl + '/api/comments';  // URL to web api

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) { }

  /** POST: add a new comment to the server */
  addComment(comment: userComment): Observable<userComment> {
    return this.http.post<userComment>(this.commentsUrl, comment, this.httpOptions);
  }

  // Obtain all comments written by a user
  getUserComments(id: string): Observable<userComment[]> {
    const url = `${this.commentsUrl}/user/${id}`;
    return this.http.get<userComment[]>(url).pipe(
      catchError(this.handleError<userComment[]>(`getComments id=${id}`))
    );
  }

  //Obtain all comments written about a song
  getSongComments(id: string): Observable<userComment[]> {
    const url = `${this.commentsUrl}/song/${id}`;
    return this.http.get<userComment[]>(url).pipe(
      catchError(this.handleError<userComment[]>(`getComments id=${id}`))
    );
  }

  /* PUT: update the comment on the server */
  updateComment(comment: userComment): Observable<any> {
    return this.http.put(this.commentsUrl, comment, this.httpOptions).pipe(
      catchError(this.handleError<any>('updateComment'))
    );
  }

  /** DELETE: delete the comment from the server */
  deleteComment(id: number): Observable<any> {
    const url = `${this.commentsUrl}/${id}`;

    return this.http.delete<userComment>(url, this.httpOptions);
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
      window.alert("failed operation")
      console.error(error); // log to console instead

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
