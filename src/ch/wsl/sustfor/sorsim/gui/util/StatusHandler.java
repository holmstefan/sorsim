/*******************************************************************************
 * Copyright 2024 Stefan Holm
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ch.wsl.sustfor.sorsim.gui.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author Stefan Holm
 *
 */
public class StatusHandler{
	
	private static Set<IStatusSubscriber> subscribers = new HashSet<IStatusSubscriber>();
	
	public static void publishText(String text) {
		Iterator<IStatusSubscriber> it = subscribers.iterator();
		while (it.hasNext()) {
			it.next().setText(text);
		}
	}
	
	public static void publishProgress(double progress) {
		Iterator<IStatusSubscriber> it = subscribers.iterator();
		while (it.hasNext()) {
			it.next().setProgress(progress);
		}		
	}
	
	public static void subscribe(IStatusSubscriber subscriber) {
		subscribers.add(subscriber);
	}
	
	public static void unsubscribe(IStatusSubscriber subscriber) {
		subscribers.remove(subscriber);
	}
}
