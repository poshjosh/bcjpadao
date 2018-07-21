/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.tasktracker.jpa.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 10, 2017 2:49:26 PM
 */
@Entity
@Table(name = "taskresponse")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Taskresponse.findAll", query = "SELECT t FROM Taskresponse t"),
    @NamedQuery(name = "Taskresponse.findByTaskresponseid", query = "SELECT t FROM Taskresponse t WHERE t.taskresponseid = :taskresponseid"),
    @NamedQuery(name = "Taskresponse.findByResponse", query = "SELECT t FROM Taskresponse t WHERE t.response = :response"),
    @NamedQuery(name = "Taskresponse.findByDeadline", query = "SELECT t FROM Taskresponse t WHERE t.deadline = :deadline"),
    @NamedQuery(name = "Taskresponse.findByTimecreated", query = "SELECT t FROM Taskresponse t WHERE t.timecreated = :timecreated"),
    @NamedQuery(name = "Taskresponse.findByTimemodified", query = "SELECT t FROM Taskresponse t WHERE t.timemodified = :timemodified")})
public class Taskresponse implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "taskresponseid")
    private Integer taskresponseid;
    @Basic(optional = false)
    @Column(name = "response")
    private String response;
    @Column(name = "deadline")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;
    @Basic(optional = false)
    @Column(name = "timecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @JoinColumn(name = "task", referencedColumnName = "taskid")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Task task;
    @JoinColumn(name = "author", referencedColumnName = "appointmentid")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Appointment author;

    public Taskresponse() {
    }

    public Taskresponse(Integer taskresponseid) {
        this.taskresponseid = taskresponseid;
    }

    public Taskresponse(Integer taskresponseid, String response, Date timecreated, Date timemodified) {
        this.taskresponseid = taskresponseid;
        this.response = response;
        this.timecreated = timecreated;
        this.timemodified = timemodified;
    }

    public Integer getTaskresponseid() {
        return taskresponseid;
    }

    public void setTaskresponseid(Integer taskresponseid) {
        this.taskresponseid = taskresponseid;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getTimecreated() {
        return timecreated;
    }

    public void setTimecreated(Date timecreated) {
        this.timecreated = timecreated;
    }

    public Date getTimemodified() {
        return timemodified;
    }

    public void setTimemodified(Date timemodified) {
        this.timemodified = timemodified;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Appointment getAuthor() {
        return author;
    }

    public void setAuthor(Appointment author) {
        this.author = author;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (taskresponseid != null ? taskresponseid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Taskresponse)) {
            return false;
        }
        Taskresponse other = (Taskresponse) object;
        if ((this.taskresponseid == null && other.taskresponseid != null) || (this.taskresponseid != null && !this.taskresponseid.equals(other.taskresponseid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[ taskresponseid=" + taskresponseid + " ]";
    }

}
