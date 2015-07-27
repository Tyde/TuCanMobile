/**
 *	This file is part of TuCan Mobile.
 *
 *	TuCan Mobile is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	TuCan Mobile is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with TuCan Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dalthed.tucan.exceptions;

public class LostSessionException extends Exception {

	public LostSessionException() {
		super();
	}

	public LostSessionException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public LostSessionException(String detailMessage) {
		super(detailMessage);
	}

	public LostSessionException(Throwable throwable) {
		super(throwable);
	}

}
