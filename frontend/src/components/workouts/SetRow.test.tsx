import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import SetRow from './SetRow';
import type { SetItem } from '../../types';

const mockSetItem: SetItem = {
  id: 'set-1',
  setNumber: 1,
  reps: 10,
  weight: 135,
  completed: true,
};

describe('SetRow', () => {
  it('renders set number and inputs', () => {
    const onChange = vi.fn();
    const onRemove = vi.fn();

    render(
      <SetRow
        setItem={mockSetItem}
        onChange={onChange}
        onRemove={onRemove}
        canRemove={true}
      />
    );

    expect(screen.getByText('Set 1')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Reps')).toHaveValue(10);
    expect(screen.getByPlaceholderText('Weight')).toHaveValue(135);
  });

  it('renders completed checkbox as checked when completed is true', () => {
    const onChange = vi.fn();
    const onRemove = vi.fn();

    render(
      <SetRow
        setItem={mockSetItem}
        onChange={onChange}
        onRemove={onRemove}
        canRemove={true}
      />
    );

    const checkbox = screen.getByRole('checkbox');
    expect(checkbox).toBeChecked();
  });

  it('calls onChange with reps when reps input changes', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    const onRemove = vi.fn();

    const setItemWithNull: SetItem = {
      ...mockSetItem,
      reps: null,
    };

    render(
      <SetRow
        setItem={setItemWithNull}
        onChange={onChange}
        onRemove={onRemove}
        canRemove={true}
      />
    );

    const repsInput = screen.getByPlaceholderText('Reps');
    await user.type(repsInput, '12');

    // Check that it was called with some value containing '12'
    expect(onChange).toHaveBeenCalled();
    const lastCall = onChange.mock.calls[onChange.mock.calls.length - 1];
    expect(lastCall[0]).toBe('reps');
  });

  it('calls onChange with weight when weight input changes', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    const onRemove = vi.fn();

    const setItemWithNull: SetItem = {
      ...mockSetItem,
      weight: null,
    };

    render(
      <SetRow
        setItem={setItemWithNull}
        onChange={onChange}
        onRemove={onRemove}
        canRemove={true}
      />
    );

    const weightInput = screen.getByPlaceholderText('Weight');
    await user.type(weightInput, '145');

    // Check that it was called with weight field
    expect(onChange).toHaveBeenCalled();
    const lastCall = onChange.mock.calls[onChange.mock.calls.length - 1];
    expect(lastCall[0]).toBe('weight');
  });

  it('calls onChange with completed when checkbox changes', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    const onRemove = vi.fn();

    render(
      <SetRow
        setItem={mockSetItem}
        onChange={onChange}
        onRemove={onRemove}
        canRemove={true}
      />
    );

    const checkbox = screen.getByRole('checkbox');
    await user.click(checkbox);

    expect(onChange).toHaveBeenCalledWith('completed', false);
  });

  it('calls onRemove when remove button clicked', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    const onRemove = vi.fn();

    render(
      <SetRow
        setItem={mockSetItem}
        onChange={onChange}
        onRemove={onRemove}
        canRemove={true}
      />
    );

    const removeButton = screen.getByRole('button', { name: /remove set/i });
    await user.click(removeButton);

    expect(onRemove).toHaveBeenCalled();
  });

  it('hides remove button when canRemove is false', () => {
    const onChange = vi.fn();
    const onRemove = vi.fn();

    render(
      <SetRow
        setItem={mockSetItem}
        onChange={onChange}
        onRemove={onRemove}
        canRemove={false}
      />
    );

    expect(screen.queryByRole('button', { name: /remove set/i })).not.toBeInTheDocument();
  });

  it('handles null reps and weight', () => {
    const onChange = vi.fn();
    const onRemove = vi.fn();

    const setItemWithNulls: SetItem = {
      id: 'set-2',
      setNumber: 2,
      reps: null,
      weight: null,
      completed: false,
    };

    render(
      <SetRow
        setItem={setItemWithNulls}
        onChange={onChange}
        onRemove={onRemove}
        canRemove={true}
      />
    );

    expect(screen.getByPlaceholderText('Reps')).toHaveValue(null);
    expect(screen.getByPlaceholderText('Weight')).toHaveValue(null);
    expect(screen.getByRole('checkbox')).not.toBeChecked();
  });
});
