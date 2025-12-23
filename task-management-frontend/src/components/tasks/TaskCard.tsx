import { useState } from 'react';
import { Trash2, Calendar, CheckCircle, Circle, RotateCcw, Edit2 } from 'lucide-react';
import type { Task } from '../../types';
import { format } from 'date-fns';
import ConfirmModal from '../ui/ConfirmModal';

interface TaskCardProps {
  task: Task;
  onToggle: (id: number, completed: boolean) => void;
  onDelete: (id: number) => void;
  onEdit: (task: Task) => void;
}

const TaskCard = ({ task, onToggle, onDelete, onEdit }: TaskCardProps) => {
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  return (
    <>
      <div className={`bg-white rounded-xl shadow-lg p-5 border-l-4 hover:shadow-xl transition-all ${
        task.completed ? 'border-green-500' : task.overdue ? 'border-red-500' : 'border-blue-500'
      }`}>
        <div className="flex items-start justify-between">
          <div className="flex items-start space-x-3 flex-1">
            <button
              onClick={() => onToggle(task.id, !task.completed)}
              className="mt-1 flex-shrink-0 group"
              title={task.completed ? 'Mark as incomplete' : 'Mark as completed'}
            >
              {task.completed ? (
                <CheckCircle className="w-6 h-6 text-green-600 group-hover:text-green-700 transition-colors" strokeWidth={2.5} />
              ) : (
                <Circle className="w-6 h-6 text-gray-400 group-hover:text-blue-600 transition-colors" strokeWidth={2.5} />
              )}
            </button>
            
            <div className="flex-1">
              <h4 className={`font-semibold text-lg ${
                task.completed ? 'text-gray-500 line-through' : 'text-gray-900'
              }`}>
                {task.title}
              </h4>
              {task.description && (
                <p className="text-sm text-gray-600 mt-1">{task.description}</p>
              )}
              
              <div className="flex flex-wrap items-center gap-4 mt-3">
                {task.dueDate && (
                  <div className={`flex items-center gap-1.5 text-sm ${
                    task.overdue && !task.completed ? 'text-red-600 font-semibold' : 'text-gray-500'
                  }`}>
                    <Calendar size={16} />
                    <span>Due: {format(new Date(task.dueDate), 'MMM dd, yyyy')}</span>
                    {task.overdue && !task.completed && (
                      <span className="px-2 py-1 bg-red-100 text-red-700 rounded-full text-xs font-bold">
                        Overdue
                      </span>
                    )}
                  </div>
                )}
                
                {task.completed && task.completedAt && (
                  <div className="flex items-center gap-1.5 text-sm text-green-600">
                    <CheckCircle size={16} />
                    <span>Completed on {format(new Date(task.completedAt), 'MMM dd, yyyy')}</span>
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className="flex items-center space-x-2">
            <button
              onClick={() => onEdit(task)}
              className="p-2 text-blue-600 hover:text-blue-700 hover:bg-blue-50 rounded-lg transition-all"
              title="Edit task"
            >
              <Edit2 size={20} />
            </button>
            
            {task.completed && (
              <button
                onClick={() => onToggle(task.id, false)}
                className="p-2 text-yellow-600 hover:text-yellow-700 hover:bg-yellow-50 rounded-lg transition-all"
                title="Mark as incomplete"
              >
                <RotateCcw size={20} />
              </button>
            )}
            
            <button
              onClick={() => setShowDeleteModal(true)}
              className="p-2 text-red-600 hover:text-red-700 hover:bg-red-50 rounded-lg transition-all"
              title="Delete task"
            >
              <Trash2 size={20} />
            </button>
          </div>
        </div>
      </div>

      <ConfirmModal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        onConfirm={() => onDelete(task.id)}
        title="Delete Task"
        message={`Are you sure you want to delete "${task.title}"? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        type="danger"
      />
    </>
  );
};

export default TaskCard;
