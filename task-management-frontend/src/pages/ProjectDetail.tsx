import { useState, useEffect, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FiArrowLeft, FiPlus, FiFilter } from 'react-icons/fi';
import Layout from '../components/layout/Layout';
import TaskCard from '../components/tasks/TaskCard';
import TaskForm from '../components/tasks/TaskForm';
import SearchBar from '../components/ui/SearchBar';
import Pagination from '../components/ui/Pagination';
import { usePagination } from '../hooks/usePagination';
import { projectsAPI, tasksAPI } from '../services/api';
import type { Project, Task, CreateTaskRequest } from '../types';
import { toast } from 'sonner';

const ProjectDetail = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [project, setProject] = useState<Project | null>(null);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [creating, setCreating] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [filterStatus, setFilterStatus] = useState<'all' | 'active' | 'completed' | 'overdue'>('all');

  useEffect(() => {
    if (id) {
      loadData();
    }
  }, [id]);

  const loadData = async () => {
    try {
      const [projectData, tasksData] = await Promise.all([
        projectsAPI.getById(Number(id)),
        tasksAPI.getAll(Number(id)),
      ]);
      setProject(projectData);
      setTasks(tasksData);
    } catch (error) {
      toast.error('Failed to load project details');
      navigate('/dashboard');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateTask = async (data: CreateTaskRequest) => {
    setCreating(true);
    try {
      const newTask = await tasksAPI.create(Number(id), data);
      setTasks([newTask, ...tasks]);
      setShowForm(false);
      toast.success('Task created successfully!');
      const updatedProject = await projectsAPI.getById(Number(id));
      setProject(updatedProject);
    } catch (error) {
      toast.error('Failed to create task');
    } finally {
      setCreating(false);
    }
  };

  const handleUpdateTask = async (data: CreateTaskRequest) => {
    if (!editingTask) return;
    
    setCreating(true);
    try {
      const updatedTask = await tasksAPI.update(Number(id), editingTask.id, {
        ...data,
        completed: editingTask.completed, // Keep current completion status
      });
      setTasks(tasks.map(t => t.id === editingTask.id ? updatedTask : t));
      setEditingTask(null);
      setShowForm(false);
      toast.success('Task updated successfully!');
      const updatedProject = await projectsAPI.getById(Number(id));
      setProject(updatedProject);
    } catch (error) {
      toast.error('Failed to update task');
    } finally {
      setCreating(false);
    }
  };

  const handleEdit = (task: Task) => {
    setEditingTask(task);
    setShowForm(true);
  };

  const handleCancelForm = () => {
    setShowForm(false);
    setEditingTask(null);
  };

  const handleToggleTask = async (taskId: number, completed: boolean) => {
    const task = tasks.find(t => t.id === taskId);
    if (!task) return;

    try {
      let updatedTask;
      if (completed) {
        // Use the complete endpoint
        updatedTask = await tasksAPI.markComplete(Number(id), taskId);
      } else {
        // Use PUT with full task data to mark as incomplete
        updatedTask = await tasksAPI.update(Number(id), taskId, {
          title: task.title,
          description: task.description,
          dueDate: task.dueDate || undefined,
          completed: false,
        });
      }
      
      setTasks(tasks.map(t => t.id === taskId ? updatedTask : t));
      const updatedProject = await projectsAPI.getById(Number(id));
      setProject(updatedProject);
      toast.success(completed ? 'Task marked as completed!' : 'Task marked as incomplete!');
    } catch (error) {
      toast.error('Failed to update task');
    }
  };

  const handleDeleteTask = async (taskId: number) => {
    try {
      await tasksAPI.delete(Number(id), taskId);
      setTasks(tasks.filter(t => t.id !== taskId));
      const updatedProject = await projectsAPI.getById(Number(id));
      setProject(updatedProject);
      toast.success('Task deleted successfully');
    } catch (error) {
      toast.error('Failed to delete task');
    }
  };

  // Filter and search logic
  const filteredTasks = useMemo(() => {
    let filtered = tasks;

    // Apply search
    if (searchQuery) {
      filtered = filtered.filter(
        (t) =>
          t.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
          t.description.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    // Apply status filter
    if (filterStatus === 'completed') {
      filtered = filtered.filter((t) => t.completed);
    } else if (filterStatus === 'active') {
      filtered = filtered.filter((t) => !t.completed);
    } else if (filterStatus === 'overdue') {
      filtered = filtered.filter((t) => t.overdue && !t.completed);
    }

    return filtered;
  }, [tasks, searchQuery, filterStatus]);

  // Pagination
  const {
    currentPage,
    totalPages,
    paginatedItems,
    goToPage,
    itemsPerPage,
    totalItems,
  } = usePagination({ items: filteredTasks, itemsPerPage: 10 });

  if (loading) {
    return (
      <Layout>
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        </div>
      </Layout>
    );
  }

  if (!project) {
    return null;
  }

  return (
    <Layout>
      <div className="mb-8">
        <button
          onClick={() => navigate('/dashboard')}
          className="flex items-center space-x-2 text-gray-600 hover:text-gray-900 mb-4 transition-colors"
        >
          <FiArrowLeft className="w-5 h-5" />
          <span className="font-medium">Back to Projects</span>
        </button>

        <div className="bg-gradient-to-r from-primary-50 to-primary-100 rounded-xl shadow-md p-6 mb-6 border border-primary-200">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">{project.title}</h1>
          <p className="text-gray-700 mb-4">{project.description}</p>

          <div className="mb-4">
            <div className="flex justify-between text-sm text-gray-700 mb-2">
              <span className="font-medium">Project Progress</span>
              <span className="font-bold text-primary-700">{project.progressPercentage.toFixed(0)}%</span>
            </div>
            <div className="w-full bg-white rounded-full h-4 overflow-hidden shadow-inner">
              <div
                className="bg-gradient-to-r from-green-500 to-green-600 h-4 rounded-full transition-all duration-500 ease-out"
                style={{ width: `${project.progressPercentage}%` }}
              />
            </div>
          </div>

          <div className="flex items-center space-x-4 text-sm text-gray-700">
            <span className="font-semibold">
              {project.completedTasks} / {project.totalTasks} tasks completed
            </span>
          </div>
        </div>

        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6 space-y-4 sm:space-y-0">
          <h2 className="text-2xl font-bold text-gray-900">Tasks</h2>
          <button
            onClick={() => {
              setEditingTask(null);
              setShowForm(!showForm);
            }}
            className="flex items-center space-x-2 bg-gradient-to-r from-emerald-500 to-emerald-600 text-white px-6 py-3 rounded-lg hover:from-emerald-600 hover:to-emerald-700 transition-all shadow-md hover:shadow-lg active:scale-95"
          >
            <FiPlus className="w-5 h-5" />
            <span>New Task</span>
          </button>
        </div>

        {/* Search and Filter for Tasks */}
        <div className="bg-white rounded-lg shadow-md p-4 mb-6">
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="flex-1">
              <SearchBar
                value={searchQuery}
                onChange={setSearchQuery}
                placeholder="Search tasks by title or description..."
              />
            </div>
            
            <div className="flex items-center space-x-2">
              <FiFilter className="text-gray-500" />
              <select
                value={filterStatus}
                onChange={(e) => setFilterStatus(e.target.value as any)}
                className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent bg-white"
              >
                <option value="all">All Tasks</option>
                <option value="active">Active</option>
                <option value="completed">Completed</option>
                <option value="overdue">Overdue</option>
              </select>
            </div>
          </div>
          
          {searchQuery || filterStatus !== 'all' ? (
            <div className="mt-3 text-sm text-gray-600">
              Showing {filteredTasks.length} of {tasks.length} tasks
            </div>
          ) : null}
        </div>

        {showForm && (
          <div className="bg-white rounded-lg shadow-md p-6 mb-6 animate-fadeIn">
            <h3 className="text-xl font-semibold mb-4">
              {editingTask ? 'Edit Task' : 'Create New Task'}
            </h3>
            <TaskForm
              onSubmit={editingTask ? handleUpdateTask : handleCreateTask}
              onCancel={handleCancelForm}
              loading={creating}
              initialData={editingTask || undefined}
            />
          </div>
        )}
      </div>

      {filteredTasks.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg shadow">
          <h3 className="text-xl font-semibold text-gray-900 mb-2">
            {searchQuery || filterStatus !== 'all' ? 'No tasks found' : 'No tasks yet'}
          </h3>
          <p className="text-gray-600 mb-4">
            {searchQuery || filterStatus !== 'all'
              ? 'Try adjusting your search or filter'
              : 'Create your first task to get started'}
          </p>
          {!searchQuery && filterStatus === 'all' && (
            <button
              onClick={() => setShowForm(true)}
              className="inline-flex items-center space-x-2 bg-emerald-600 text-white px-6 py-3 rounded-lg hover:bg-emerald-700 transition-colors shadow-sm active:bg-emerald-800"
            >
              <FiPlus className="w-5 h-5" />
              <span>Create Task</span>
            </button>
          )}
        </div>
      ) : (
        <>
          <div className="space-y-4 mb-6">
            {paginatedItems.map((task) => (
              <TaskCard
                key={task.id}
                task={task}
                onToggle={handleToggleTask}
                onDelete={handleDeleteTask}
                onEdit={handleEdit}
              />
            ))}
          </div>

          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={goToPage}
            itemsPerPage={itemsPerPage}
            totalItems={totalItems}
          />
        </>
      )}
    </Layout>
  );
};

export default ProjectDetail;
