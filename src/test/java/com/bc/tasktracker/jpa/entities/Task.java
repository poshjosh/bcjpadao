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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 10, 2017 2:49:26 PM
 */
@Entity
@Table(name = "task")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Task.findAll", query = "SELECT t FROM Task t"),
    @NamedQuery(name = "Task.findByTaskid", query = "SELECT t FROM Task t WHERE t.taskid = :taskid"),
    @NamedQuery(name = "Task.findByDescription", query = "SELECT t FROM Task t WHERE t.description = :description"),
    @NamedQuery(name = "Task.findByTimeopened", query = "SELECT t FROM Task t WHERE t.timeopened = :timeopened"),
    @NamedQuery(name = "Task.findByTimeclosed", query = "SELECT t FROM Task t WHERE t.timeclosed = :timeclosed"),
    @NamedQuery(name = "Task.findByTimecreated", query = "SELECT t FROM Task t WHERE t.timecreated = :timecreated"),
    @NamedQuery(name = "Task.findByTimemodified", query = "SELECT t FROM Task t WHERE t.timemodified = :timemodified")})
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "taskid")
    private Integer taskid;
    @Column(name = "description")
    private String description;
    @Column(name = "timeopened")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeopened;
    @Column(name = "timeclosed")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeclosed;
    @Basic(optional = false)
    @Column(name = "timecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @JoinColumn(name = "doc", referencedColumnName = "docid")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Doc doc;
    @JoinColumn(name = "author", referencedColumnName = "appointmentid")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Appointment author;
    @JoinColumn(name = "reponsibility", referencedColumnName = "appointmentid")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Appointment reponsibility;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "task", fetch = FetchType.LAZY)
    private List<Taskresponse> taskresponseList;

    public Task() {
    }

    public Task(Integer taskid) {
        this.taskid = taskid;
    }

    public Task(Integer taskid, Date timecreated, Date timemodified) {
        this.taskid = taskid;
        this.timecreated = timecreated;
        this.timemodified = timemodified;
    }

    public Integer getTaskid() {
        return taskid;
    }

    public void setTaskid(Integer taskid) {
        this.taskid = taskid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimeopened() {
        return timeopened;
    }

    public void setTimeopened(Date timeopened) {
        this.timeopened = timeopened;
    }

    public Date getTimeclosed() {
        return timeclosed;
    }

    public void setTimeclosed(Date timeclosed) {
        this.timeclosed = timeclosed;
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

    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) {
        this.doc = doc;
    }

    public Appointment getAuthor() {
        return author;
    }

    public void setAuthor(Appointment author) {
        this.author = author;
    }

    public Appointment getReponsibility() {
        return reponsibility;
    }

    public void setReponsibility(Appointment reponsibility) {
        this.reponsibility = reponsibility;
    }

    @XmlTransient
    public List<Taskresponse> getTaskresponseList() {
        return taskresponseList;
    }

    public void setTaskresponseList(List<Taskresponse> taskresponseList) {
        this.taskresponseList = taskresponseList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (taskid != null ? taskid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Task)) {
            return false;
        }
        Task other = (Task) object;
        if ((this.taskid == null && other.taskid != null) || (this.taskid != null && !this.taskid.equals(other.taskid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[ taskid=" + taskid + " ]";
    }

}
