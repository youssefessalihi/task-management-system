import { useForm } from 'react-hook-form';
import type { CreateProjectRequest, Project } from '../../types';
import { useEffect } from 'react';

interface ProjectFormProps {
  onSubmit: (data: CreateProjectRequest) => Promise<void>;
  onCancel: () => void;
  loading?: boolean;
  initialData?: Project;
}

const ProjectForm = ({ onSubmit, onCancel, loading, initialData }: ProjectFormProps) => {
  const { register, handleSubmit, formState: { errors }, reset } = useForm<CreateProjectRequest>({
    defaultValues: initialData ? {
      title: initialData.title,
      description: initialData.description,
    } : undefined
  });

  useEffect(() => {
    if (initialData) {
      reset({
        title: initialData.title,
        description: initialData.description,
      });
    }
  }, [initialData, reset]);

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label htmlFor="title" className="block text-sm font-medium text-gray-700">
          Project Title *
        </label>
        <input
          {...register('title', { required: 'Title is required' })}
          type="text"
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 px-4 py-2 border"
          placeholder="Enter project title"
        />
        {errors.title && (
          <p className="mt-1 text-sm text-red-600">{errors.title.message}</p>
        )}
      </div>

      <div>
        <label htmlFor="description" className="block text-sm font-medium text-gray-700">
          Description
        </label>
        <textarea
          {...register('description')}
          rows={4}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 px-4 py-2 border"
          placeholder="Enter project description"
        />
      </div>

      <div className="flex space-x-3">
        <button
          type="submit"
          disabled={loading}
          className="flex-1 bg-emerald-600 text-white px-4 py-2 rounded-lg hover:bg-emerald-700 disabled:opacity-50 disabled:cursor-not-allowed font-medium transition-colors shadow-sm active:bg-emerald-800"
        >
          {loading ? (initialData ? 'Updating...' : 'Creating...') : (initialData ? 'Update Project' : 'Create Project')}
        </button>
        <button
          type="button"
          onClick={onCancel}
          className="flex-1 bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300 font-medium"
        >
          Cancel
        </button>
      </div>
    </form>
  );
};

export default ProjectForm;
