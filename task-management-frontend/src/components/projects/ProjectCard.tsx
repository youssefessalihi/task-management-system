import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Trash2, CheckCircle, Clock, Edit2 } from 'lucide-react';
import type { Project } from '../../types';
import { format } from 'date-fns';
import ConfirmModal from '../ui/ConfirmModal';

interface ProjectCardProps {
  project: Project;
  onDelete: (id: number) => void;
  onEdit: (project: Project) => void;
}

const ProjectCard = ({ project, onDelete, onEdit }: ProjectCardProps) => {
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  return (
    <>
      <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-2xl transition-all border-2 border-gray-100 hover:border-blue-200">
        <div className="flex justify-between items-start mb-4">
          <Link to={`/projects/${project.id}`} className="flex-1 group">
            <h3 className="text-xl font-bold text-gray-900 group-hover:text-blue-600 transition-colors mb-2">
              {project.title}
            </h3>
          </Link>
          <div className="flex items-center space-x-2">
            <button
              onClick={() => onEdit(project)}
              className="text-blue-600 hover:text-blue-700 hover:bg-blue-50 p-2 rounded-lg transition-all"
              title="Edit project"
            >
              <Edit2 size={20} />
            </button>
            <button
              onClick={() => setShowDeleteModal(true)}
              className="text-red-600 hover:text-red-700 hover:bg-red-50 p-2 rounded-lg transition-all"
              title="Delete project"
            >
              <Trash2 size={20} />
            </button>
          </div>
        </div>

        <p className="text-gray-600 mb-4 line-clamp-2 text-sm">
          {project.description || 'No description provided'}
        </p>

        <div className="mb-4">
          <div className="flex justify-between text-sm text-gray-600 mb-2">
            <span className="font-medium">Progress</span>
            <span className="font-bold text-blue-600">
              {project.progressPercentage.toFixed(0)}%
            </span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-3 overflow-hidden shadow-inner">
            <div
              className="bg-gradient-to-r from-blue-500 to-blue-700 h-3 rounded-full transition-all duration-500 ease-out"
              style={{ width: `${project.progressPercentage}%` }}
            />
          </div>
        </div>

        <div className="flex items-center justify-between text-sm">
          <div className="flex items-center space-x-4">
            <div className="flex items-center gap-2 text-green-600">
              <CheckCircle size={16} strokeWidth={2.5} />
              <span className="font-medium">
                {project.completedTasks}/{project.totalTasks}
              </span>
            </div>
            
            <div className="flex items-center gap-2 text-gray-500">
              <Clock size={16} />
              <span>{format(new Date(project.createdAt), 'MMM dd, yyyy')}</span>
            </div>
          </div>
        </div>
      </div>

      <ConfirmModal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        onConfirm={() => onDelete(project.id)}
        title="Delete Project"
        message={`Are you sure you want to delete "${project.title}"? This will also delete all tasks in this project. This action cannot be undone.`}
        confirmText="Delete Project"
        cancelText="Cancel"
        type="danger"
      />
    </>
  );
};

export default ProjectCard;
